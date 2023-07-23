from aifc import Error

from flask import Flask, jsonify, request, make_response
from HdRezkaAPI import HdRezkaApi
from flask_cors import CORS
from Entities import Movie, Translation, Resolution


app = Flask(__name__)
CORS(app)


@app.route('/api/movie', methods=['POST'])
def getMovie():
    data = request.get_json()
    url = data.get('url')
    try:
        movie = getMovieObject(url)
        return make_response(jsonify(movie=movie.to_dict()), 200)
    except:
        return make_response(jsonify(error='Invalid URL provided.'), 400)


def getMovieObject(url, attempts=3):
    try:
        rezka = HdRezkaApi(url)
        translations = []
        for t in rezka.getTranslations().keys():
            resolutions = []
            for res in rezka.getStream(translation=t).videos.keys():
                resolutions.append(Resolution(res))
                print(res)
            translations.append(Translation(t, resolutions))

        movie = Movie(url, rezka.getName(), translations)

        return movie
    except:
        if (attempts - 1 > 0):
            getMovieObject(url, attempts - 1)
        else:
            return None


@app.route('/api/movie/link', methods=['POST'])
def getStreamLink():
    data = request.get_json()
    url = data.get('url')
    translation = data.get('translation')
    resolution = data.get('resolution')
    try:
        rezka = HdRezkaApi(url)
        stream = rezka.getStream(translation=translation)(resolution)
        return make_response(jsonify(stream, 200))
    except:
        return make_response(jsonify(error='Invalid URL provided.'), 400)


if __name__ == '__main__':
    app.run()