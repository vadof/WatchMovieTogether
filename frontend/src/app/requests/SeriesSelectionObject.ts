import {Series} from "../models/Series";
import {SeriesTranslation} from "../models/SeriesTranslation";
import {Season} from "../models/Seson";

export interface SeriesSelectionObject {
  groupId: number
  series: Series
  selectedSeriesTranslation: SeriesTranslation
  season: Season
  episode: number;
}
