import { GeneratorFeatures } from 'yeoman-generator'
import { BaseGenerator, BaseGeneratorOptions } from '../../../base'
import _ from 'lodash'

export default class PersistenceGenerator extends BaseGenerator {
  constructor(args: string | string[], options: BaseGeneratorOptions, features?: GeneratorFeatures) {
    super(args, _.assign(options ?? {}, {
      questions: [{
        type: 'confirm',
        name: 'persistenceLayer',
        message: 'Would like to enable persistence layer?',
        default: false
      }] 
    }), features)
  }

  async prompting() {
    let answers;
    if (!this._isCronGenerator()) {
      answers = await this._internalPrompting()
    } else {
      answers = { persistenceLayer: true };
    }
    _.assign(this.options.prompting, answers)
  }

  async writing() {
    const { prompting : { microserviceType, persistenceLayer } } = this.options
    if (persistenceLayer) {
      await this._defaultInternalWriting(({filename}) => {
        if (filename === 'data.sql' && microserviceType === 'none' && !this._isSoapService() && !this._isCronGenerator()) {
          return false;
        }
        return true
      })
    }
  }
}
