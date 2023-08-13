class Movie:

    def __init__(self, link, name, translations):
        self.link = link
        self.name = name
        self.translations = translations

    def to_dict(self):
        return {
            'link': self.link,
            'name': self.name,
            'translations': [x.to_dict() for x in self.translations]
        }

    def __str__(self):
        return f'link={self.link}, name={self.name}, translations={self.translations}'


class Translation:

    def __init__(self, name, resolutions):
        self.name = name
        self.resolutions = resolutions

    def to_dict(self):
        return {
            'name': self.name,
            'resolutions': [x.to_dict() for x in self.resolutions],
        }

    def __str__(self):
        return f'name={self.name}, resolutions={self.resolutions}'


class Resolution:
    def __init__(self, value):
        self.value = value

    def to_dict(self):
        return {'value': self.value}

    def __str__(self):
        return f'value={self.value}'


class Series:

    def __init__(self, link, name, seriesTranslations):
        self.link = link
        self.name = name
        self.seriesTranslations = seriesTranslations

    def to_dict(self):
        return {
            'link': self.link,
            'name': self.name,
            'seriesTranslations': [s.to_dict() for s in self.seriesTranslations]
        }

    def __str__(self):
        return f'link={self.link}, name={self.name}, seriesTranslations={self.seriesTranslations}'


class SeriesTranslation:

    def __init__(self, name, seasons, resolutions):
        self.name = name
        self.seasons = seasons
        self.resolutions = resolutions

    def to_dict(self):
        return {
            'name': self.name,
            'seasons': [s.to_dict() for s in self.seasons],
            'resolutions': [r.to_dict() for r in self.resolutions],
        }

    def __str__(self):
        return f'name={self.name}, seasons={self.seasons}, resolutions={self.resolutions}'

class Season:

    def __init__(self, number, episodes):
        self.number = number
        self.episodes = episodes


    def to_dict(self):
        return {
            'number': self.number,
            'episodes': self.episodes
        }

    def __str__(self):
        return f'number={self.number}, episodes={self.episodes}'