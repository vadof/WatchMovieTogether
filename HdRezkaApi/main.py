from aifc import Error

from flask import Flask, jsonify, request, make_response
from HdRezkaAPI import HdRezkaApi
from flask_cors import CORS
from Entities import *


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


def getMovieObject(url, attempts=3):
    try:
        rezka = HdRezkaApi(url)
        translations = []
        for t in rezka.getTranslations().keys():
            resolutions = []
            for res in rezka.getStream(translation=t).videos.keys():
                resolutions.append(Resolution(res))
            translations.append(Translation(t, resolutions))

        movie = Movie(url, rezka.getName(), translations)

        return movie
    except:
        if (attempts - 1 > 0):
            getMovieObject(url, attempts - 1)
        else:
            return None


def getSeriesObject(url, attempts=3):
    try:
        rezka = HdRezkaApi(url)

        seriesInfo = rezka.getSeasons()
        translations = seriesInfo.keys()
        seriesTranslations = []

        for t in translations:
            translationInfo = seriesInfo.get(t)
            translationSeasons = []
            episodes = translationInfo.get('episodes')
            for s in translationInfo.get('seasons').keys():
                season = Season(s, len(episodes.get(s)))
                translationSeasons.append(season)

            resolutions = list(rezka.getStream(list(translationInfo.get('seasons').keys())[0], '1', t).videos.keys())
            translation = SeriesTranslation(t, translationSeasons, resolutions)

            seriesTranslations.append(translation)

        return Series(url, rezka.getName(), seriesTranslations)
    except:
        if (attempts - 1 > 0):
            return getSeriesObject(url, attempts - 1)
        else:
            return None


if __name__ == '__main__':
    app.run()
