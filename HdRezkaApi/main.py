from aifc import Error

from flask import Flask, jsonify, request, make_response
from HdRezkaAPI import HdRezkaApi
from flask_cors import CORS
from Entities import Movie, Translation, Resolution


app = Flask(__name__)
CORS(app)

# TODO make 3 attempts to get video
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
        rezka = HdRezkaApi('https://rezka.ag/films/action/55135-forsazh-10-2023.html')
        translations = []
        for t in rezka.getTranslations().keys():
            resolutions = []
            for res, value in rezka.getStream(translation=t).videos.items():
                resolutions.append(Resolution(res, value))
            translations.append(Translation(t, resolutions))

        movie = Movie('https://rezka.ag/films/action/55135-forsazh-10-2023.html', rezka.getName(), translations)

        for i in movie.translations:
            for x in i.resolutions:
                print(x)

        return movie
    except:
        if (attempts - 1 > 0):
            getMovieObject(url, attempts - 1)
        else:
            return None


if __name__ == '__main__':
    app.run()
