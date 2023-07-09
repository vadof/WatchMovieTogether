import {User} from "./User";
import {GroupSettings} from "./GroupSettings";

export interface Group {
  id: number
  name: string
  admin: string
  groupSettings: GroupSettings,
  users: User[]
}
