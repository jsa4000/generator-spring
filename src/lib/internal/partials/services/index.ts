import { GeneratorFeatures } from 'yeoman-generator'
import { BaseGenerator, BaseGeneratorOptions } from '../../../base'
import _ from 'lodash'

export default class ServicesGenerator extends BaseGenerator {
  constructor(args: string | string[], options: BaseGeneratorOptions, features?: GeneratorFeatures) {
    super(args, _.assign(options ?? {}, {
      questions: [
        {
          type: 'confirm',
          name: 'feignClient',
          message: 'Would you need invoke other microservices?',
          default: false,
        },
        {
          type: 'confirm',
          name: 'jaxws',
          message: 'Would you need invoke WSDL/SOAP external web services?',
          default: false,
        },
      ]
    }), features)
  }

  async prompting() {
    const answers = await this._internalPrompting()
    _.assign(this.options.prompting, answers)
  }

  async writing() {
    const {
      prompting: { microserviceType, persistenceLayer, testcontainers },
    } = this.options
    await this._defaultInternalWriting(({ filename }) => {
      if (
        (filename === 'AbstractMSSQLServerContainerTest.java' ||
          filename === 'application-testcontainers-sqlserver-test.yml') &&
        (!persistenceLayer || !testcontainers)
      ) {
        return false
      }
      if (
        (filename === 'PaginationResultDto.java' || filename.endsWith('Dto.java')) && 
        (microserviceType === 'apifirst' || this._isSoapService() || (microserviceType === 'none' && !!persistenceLayer))) {
          return false
        }
      return true
    })
  }
}
