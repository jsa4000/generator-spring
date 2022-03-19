import _ from 'lodash'
import { GeneratorFeatures } from 'yeoman-generator'
import { BaseGenerator, BaseGeneratorOptions } from '../../../base'

export default class SPIServiceGenerator extends BaseGenerator {
  constructor(args: string | string[], options: BaseGeneratorOptions, features?: GeneratorFeatures) {
    super(args, options, features)
  }

  default() {
    _.assign(this.options.prompting, { 
      microserviceType: 'none',
      bannerName: `${this.options.prompting.projectName}-spiservice` 
    })
    if (this.options.useOutDirectory) {
      this.destinationRoot(`out/${this.options.prompting.bannerName}`)
    }
    this.composeWith(this._resolveInternalPartial('persistence'), this.options)
    this.composeWith(this._resolveInternalPartial('services'), this.options)
    this.composeWith(this._resolveInternalPartial('testing'), this.options)
  }

  async writing() {
    await this._defaultInternalWriting()
  }
}
