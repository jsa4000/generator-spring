import { GeneratorFeatures, GeneratorOptions } from 'yeoman-generator'
import { BaseGenerator } from '../../../base'

export default class MicroserviceAPIFirstGenerator extends BaseGenerator {
  constructor(args: string | string[], options: GeneratorOptions, features?: GeneratorFeatures) {
    super(args, options, features)
  }

  async writing() {
    const { prompting : { microserviceType } } = this.options
    if (microserviceType === 'apifirst') {
        await this._defaultInternalWriting()
    }
  }
}
