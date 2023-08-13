import {Series} from "./Series";
import {Season} from "./Seson";
import {SeriesTranslation} from "./SeriesTranslation";

export interface SeriesSettings {
  selectedSeries: Series;
  selectedTranslation: SeriesTranslation;
  selectedSeason: Season;
  selectedEpisode: number;
}
