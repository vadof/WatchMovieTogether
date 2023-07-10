import {User} from "./User";

export interface Message {
  user: User,
  message: string
  time: number
}
