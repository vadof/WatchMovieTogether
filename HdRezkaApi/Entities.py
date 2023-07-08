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
        return 'link=' + self.link + ',' + \
                'name=' + self.name + ',' + \
                'translations=' + self.translations


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
        return 'name=' + self.name + ',' + \
            'resolutions=' + self.resolutions


class Resolution:
    def __init__(self, value, videoLink):
        self.value = value
        self.videoLink = videoLink

    def to_dict(self):
        return {
            'value': self.value,
            'videoLink': self.videoLink
        }

    def __str__(self):
        return 'value=' + self.value + ',' + \
                'videoLink=' + self.videoLink
