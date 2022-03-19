import { GeneratorFeatures } from 'yeoman-generator'
import { BaseGenerator, BaseGeneratorOptions } from '../../../base'
import _ from 'lodash'

const MICROSERVICE_TYPE_ANSWER = {
  type: 'list',
  name: 'microserviceType',
  message: 'Select the strategy in order to develop this microservice',
  choices: [
    {
      value: 'apifirst',
      name: 'API-First',
    },
    {
      value: 'codefirst',
      name: 'Code First',
    },
  ],
  default: 'apifirst',
};

export default class MicroserviceGenerator extends BaseGenerator {
  constructor(args: string | string[], options: BaseGeneratorOptions, features?: GeneratorFeatures) {
    super(args, _.assign(options ?? {}, {
      questions: [ MICROSERVICE_TYPE_ANSWER ]
    }), features)
  }

  async prompting() {
    const answers = await this._internalPrompting()
    _.assign(this.options.prompting, answers, { bannerName: `${this.options.prompting.projectName}-microservice` })
  }

  default() {
    if (this.options.useOutDirectory) {
      this.destinationRoot(`out/${this.options.prompting.bannerName}`)
    }
    this.composeWith(this._resolveInternalPartial('e2e'), this.options)
    MICROSERVICE_TYPE_ANSWER.choices.forEach(choice => {
      this.composeWith(this._resolveInternalPartial(choice.value), this.options)  
    });
    this.composeWith(this._resolveInternalPartial('persistence'), this.options)
    this.composeWith(this._resolveInternalPartial('services'), this.options)
    this.composeWith(this._resolveInternalPartial('testing'), this.options)
  }

  async writing() {
    await this._defaultInternalWriting()
  }
}
