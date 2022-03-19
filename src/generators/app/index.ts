import { GeneratorFeatures } from 'yeoman-generator'
import { BaseGenerator, BaseGeneratorOptions } from '../../lib/base'
import { green } from 'chalk'
import moment from 'moment'
import yosay from 'yosay'
import _ from 'lodash'

export default class MainGenerator extends BaseGenerator {
  // The name `constructor` is important here
  constructor(args: string | string[], options: BaseGeneratorOptions, features?: GeneratorFeatures) {
    super(args, _.assign(options ?? {}, {
      questions: [
        {
          type: 'list',
          name: 'appType',
          message: 'Select type of artifact in order to generate',
          choices: [
            {
              value: 'micro',
              name: 'Microservice',
            },
            {
              value: 'soap-service',
              name: 'SOAP WebService',
            },
            {
              value: 'spi-service',
              name: 'SPI Service',
            },
            {
              value: 'cron',
              name: 'Background cron process',
            },
          ],
        },
        {
          type: 'input',
          name: 'projectName',
          message: 'Enter the project name',
          default: 'sample',
        }
      ]
    }), features)
  }

  initializing(): void {
    this.log(yosay(`Welcome to ${green('Spring')} generator!`))
    if (_.isNil(this.options.prompting)) {
      this.options.prompting = {}
    }
  }

  async prompting() {
    const { appType, projectName } = await this._internalPrompting()
    const projectNameCamelCase = _.camelCase(projectName)
    const projectNameKebabCaseLowerCase = _.kebabCase(projectName).toLowerCase()
    const projectNameSnakeCase = _.snakeCase(projectName)
    _.assign(this.options.prompting, {
      projectName: projectNameKebabCaseLowerCase,
      artifactId: projectNameCamelCase.toLowerCase(),
      preffixClassName: this._capitalizeFirstLetter(projectNameCamelCase),
      preffixVariableName: projectNameCamelCase,
      entityTableName: projectNameSnakeCase.toUpperCase(),
      entityTableNameLowerCase: projectNameSnakeCase.toLowerCase(),
      entityAbbrInJPQL: projectNameKebabCaseLowerCase
        .split('-')
        .map((value) => value.charAt(0))
        .join(''),
      runtimeExecutionTimestamp: moment().format('YYYYMMDDHHmmssSSS'),
    })
    this.composeWith(this._resolveInternalGeneratorByAppType(appType), this.options)
  }
}
