import { Injectable } from '@angular/core';

const VOLUME_KEY = "PREFERRED_VOLUME"
const RESOLUTION_KEY = "PREFERRED_RESOLUTION"

@Injectable({
  providedIn: 'root'
})
export class UserConfigService {
  constructor() { }

  public setPreferredVolume(value: number) {
    localStorage.setItem(VOLUME_KEY, value + '');
  }

  public getPreferredVolume(): number | null {
    let volume = localStorage.getItem(VOLUME_KEY);
    if (volume) {
      return parseFloat(volume);
    } else {
      return null;
    }
  }

  public setPreferredResolution(resolutionValue: string) {
    localStorage.setItem(RESOLUTION_KEY, resolutionValue)
  }

  public getPreferredResolution(): string | null {
    return localStorage.getItem(RESOLUTION_KEY)
  }
}
