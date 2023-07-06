from flask import Flask, jsonify, request, make_response
from HdRezkaAPI import HdRezkaApi
from flask_cors import CORS

from Movie import Movie
from Translation import Translation


app = Flask(__name__)
CORS(app)


@app.route('/api/movie', methods=['POST'])
def square():
    data = request.get_json()
    url = data.get('url')
    try:
        movie = getMovie(url)
        response = make_response(jsonify(movie=movie.to_dict()), 200)
        return response
    except:
        response = make_response(jsonify(error='Invalid URL provided.'), 400)
        return response


def getMovie(url):
    rezka = HdRezkaApi(url)

    type = rezka.getType().split('.')[1]

    if type == 'movie':
        resolutions = []
        for key in rezka.getStream().videos.keys():
            resolutions.append(key)

        translations = []
        for key, value in rezka.getTranslations().items():
            translations.append(Translation(key, value))

        return Movie(url, rezka.getName(), resolutions, translations)

if __name__ == '__main__':
    app.run()
