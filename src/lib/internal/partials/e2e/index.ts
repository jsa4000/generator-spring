import { BaseGenerator } from '../../../base'
import { GeneratorFeatures, GeneratorOptions } from 'yeoman-generator'
import art from 'ascii-art'

const BANNER_ALT = "-- Generate banner with https://devops.datenkollektiv.de/banner.txt/index.html (Banner Font -> 'stop')"

export default class E2EGenerator extends BaseGenerator {
  constructor(args: string | string[], options: GeneratorOptions, features?: GeneratorFeatures) {
    super(args, options, features)
  }
  async initializing(): Promise<void> {
    const { prompting } = this.options;
    try {
        const { bannerName, projectName } = prompting
        prompting.bannerArt = await art.font(bannerName ?? projectName, 'doom');
    } catch (e) {
        prompting.bannerArt = BANNER_ALT;
    }
  }

  async writing() {
    await this._defaultInternalWriting()
  }
}
