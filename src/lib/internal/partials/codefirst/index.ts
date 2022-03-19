import { GeneratorFeatures, GeneratorOptions } from 'yeoman-generator'
import { BaseGenerator } from '../../../base'

export default class MicroserviceCodeFirstGenerator extends BaseGenerator {
  constructor(args: string | string[], options: GeneratorOptions, features?: GeneratorFeatures) {
    super(args, options, features)
  }

  async writing() {
    const { prompting : { microserviceType, persistenceLayer } } = this.options
    if (microserviceType === 'codefirst') {
        await this._defaultInternalWriting(({ filename }) => {
          if (filename === 'PaginationResult.java' && persistenceLayer) {
            return false;
          }
          return true;
        })
    }
  }
}
