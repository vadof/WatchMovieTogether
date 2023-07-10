import {User} from "./User";
import {GroupSettings} from "./GroupSettings";
import {Chat} from "./Chat";

export interface Group {
  id: number
  name: string
  admin: string
  groupSettings: GroupSettings,
  users: User[]
}
