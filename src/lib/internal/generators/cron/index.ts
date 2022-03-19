import { GeneratorFeatures } from 'yeoman-generator'
import { BaseGenerator, BaseGeneratorOptions } from '../../../base'

import _ from 'lodash'

export default class CronGenerator extends BaseGenerator {
  constructor(args: string | string[], options: BaseGeneratorOptions, features?: GeneratorFeatures) {
    super(args, options, features)
  }

  default() {
    _.assign(this.options.prompting, { 
      microserviceType: 'none',
      scheduledCron: true,
      bannerName: `${this.options.prompting.projectName}-cron` 
    })
    if (this.options.useOutDirectory) {
      this.destinationRoot(`out/${this.options.prompting.bannerName}`)
    }
    this.composeWith(this._resolveInternalPartial('e2e'), this.options)
    this.composeWith(this._resolveInternalPartial('services'), this.options)
    this.composeWith(this._resolveInternalPartial('persistence'), this.options)
    this.composeWith(this._resolveInternalPartial('testing'), this.options)
  }

  async writing() {
    await this._defaultInternalWriting()
  }
}