class Movie:

    def __init__(self, link, name, resolutions, translations):
        self.link = link
        self.name = name
        self.resolutions = resolutions
        self.translations = translations

    def to_dict(self):
        return {
            'link': self.link,
            'name': self.name,
            'resolutions': self.resolutions,
            'translations': [x.to_dict() for x in self.translations]
        }
