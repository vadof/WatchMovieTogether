import {SeriesTranslation} from "./SeriesTranslation";

export interface Series {
  type: 'series'
  link: string;
  name: string;
  seriesTranslations: SeriesTranslation[];
}
