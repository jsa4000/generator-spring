import _ from 'lodash'
import { GeneratorFeatures } from 'yeoman-generator'
import { BaseGenerator, BaseGeneratorOptions } from '../../../base'

export default class TestingGenerator extends BaseGenerator {
  constructor(args: string | string[], options: BaseGeneratorOptions, features?: GeneratorFeatures) {
    super(args, _.assign(options ?? {}, {
      questions: [
        {
          type: 'confirm',
          name: 'testcontainers',
          message: 'Would like to enable Testcontainers framework testing?',
        },
        {
          type: 'confirm',
          name: 'cucumberTest',
          message: 'Would like to enable Cucumber BDD framework testing? (experimental)',
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
      /* RESTful API conditions */
      if (filename.endsWith('DelegateTest.java') && (microserviceType === 'codefirst' || microserviceType === 'none')) {
        return false;
      }
      if (filename.endsWith('ControllerTest.java') && (microserviceType === 'apifirst' || microserviceType === 'none')) {
        return false;
      }
      /* Service tests conditions */ 
      if (
        (filename === 'AbstractMSSQLServerContainerTest.java' ||
          filename === 'application-testcontainers-sqlserver-test.yml' ||
          filename.endsWith('ServiceITTest.java')) &&
        (!persistenceLayer || !testcontainers)
      ) {
        return false
      }
      // *ServiceITTest.java
      if (this._isCronGenerator()) {
        if(!filename.endsWith('CronServiceITTest.java') && (filename.endsWith('ServiceITTest.java') || filename.endsWith('ServiceTest.java'))) {
            return false
        }
      // *CronServiceITTest.java
      } else if(filename.endsWith('CronServiceITTest.java')) {
        return false;
      }
      // *MotherObject.java
      if (
        !filename.endsWith('DtoMotherObject.java') &&
        filename.endsWith('MotherObject.java') &&
        !persistenceLayer
      ) {
        return false
      }
      // *Dto.java
      if (filename.endsWith('Dto.java') 
            && (this._isSoapService() || ((microserviceType !== 'none' || !persistenceLayer) && !this._isCronGenerator()))) {
        return false;
      }
      // *WSImplTest.java
      if (filename.endsWith('WsImplTest.java') && !this._isSoapService()) {
        return false
      }
      return true
    })
  }
}
