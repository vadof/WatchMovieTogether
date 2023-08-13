import {Resolution} from "./Resolution";
import {Season} from "./Seson";

export interface SeriesTranslation {
   name: string;
   seasons: Season[];
   resolutions: Resolution[];
}
