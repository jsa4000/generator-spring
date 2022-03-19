import Generator, { Answers, GeneratorFeatures } from 'yeoman-generator'
import { BaseGeneratorOptions } from './base-generator-options'
import { red, green, blue, yellow } from 'chalk'
import fs from 'fs/promises'
import path from 'path'
import _ from 'lodash'

export type TemplateWritingConditionFunction = (template: TemplatePathInfo) => boolean
export interface TemplatePathInfo {
  templatesDirectoryAbsolutePath: string
  absolutePath: string
  relativePath: string
  filename: string
}

const EXCLUDE_FILES_TO_WRITE = ['.DS_Store']

export interface InternalPromptingResult {
  /**
   * Gets or sets additional properties.
   */
  [name: string]: any
}

export class BaseGenerator<T extends BaseGeneratorOptions = BaseGeneratorOptions> extends Generator<T> {

  constructor(args: string | string[], options: T, features?: GeneratorFeatures) {
    super(args, options, features)
    this.option('cli', {
      type: Boolean,
      description: 'Enable CLI mode'
    })
    this.option('useOutDirectory', {
      type: Boolean, 
      description: 'Generator will use the ./out/${projectName} in order to deposite the result files'
    })
    if (options.questions != null && !!options.questions.length) {
      for (const { name, message, type } of options.questions) {
        const kindOfOption = type === 'confirm' ? Boolean : String
        this.option(name as string, {
          type: kindOfOption,
          description: message as string,
        })
      }
    }
  }

  protected async _internalPrompting(): Promise<InternalPromptingResult> {
    const promptingWithoutOptions: any[] = []
    const answers = {}
    if (this.options.questions != null && !!this.options.questions.length) {
      for (const question of this.options.questions) {
        const { type, name, choices } = question
        let { default: originalDefaultValue } = question
        if (type === 'confirm' && _.isUndefined(originalDefaultValue)) {
          originalDefaultValue = true
        }
        const optionValue = this.options[name as string];
        const validValues = (choices ?? []).map(choice => choice.value).join(', ');
        if (!_.isNil(optionValue)) {
          if (type === 'list' && !_.isEmpty(choices) && !choices.some((choice) => optionValue === choice.value.toString() )) {
            this.log(red(`X The option '--${name}' has a unknown value ('${optionValue}'). The valid values are: ${validValues}`))
            promptingWithoutOptions.push(question)
          } else {
            this.log(green(`+ The value '${optionValue}' was assigned to option --${name}. Skipping prompting for this option..${!_.isEmpty(validValues) ? ' Remember, the valid values were: ' + validValues : ''}`));
            _.assign(answers, { [name as string]: optionValue })
          }
        } else if (!!this.options.cli && (type === 'confirm' || !_.isNil(originalDefaultValue))) {
          // eslint-disable-next-line no-debugger
          let defaultValue : any;
          if (type === 'confirm') {
            defaultValue = false;
          } 
          if (!_.isNil(originalDefaultValue)) {
            defaultValue = originalDefaultValue;           
          }
          this.log(blue(`- The default value '${defaultValue}' was assigned to option --${name}. Skipping prompting for this option...`));
          _.assign(answers, { [name as string]: defaultValue })
        } else {
          promptingWithoutOptions.push(question)
        }
      }
      if (!_.isEmpty(promptingWithoutOptions)) {
        const answersByPrompt = await this.prompt<Answers>(promptingWithoutOptions)
        _.assign(answers, answersByPrompt)
      }
    }
    return answers
  }

  protected async _defaultInternalWriting(testFn?: TemplateWritingConditionFunction) {
    const templates = await this._getAllTemplates()
    if (!_.isEmpty(templates)) {
      templates
        .filter((currentTemplate) => (_.isFunction(testFn) ? testFn(currentTemplate) : true))
        .forEach((currentTemplate) => {
          const finalRelativeDestinationPath =
            this._calculateFinalRelativeDestinationPathTemplate(currentTemplate)
          this.fs.copyTpl(
            currentTemplate.absolutePath,
            this.destinationPath(finalRelativeDestinationPath),
            _.assign({}, this.options.prompting, { __self__: this }),
          )
        })
    }
  }

  protected async _getAllTemplates(): Promise<Array<TemplatePathInfo>> {
    const templatesDirectory = this.templatePath()
    const allTemplates = await this._getAllRegularFilesFromDirectory(templatesDirectory)
    const result = allTemplates.map((templatePath) => {
      return {
        templatesDirectoryAbsolutePath: templatesDirectory,
        absolutePath: templatePath,
        relativePath: path.relative(templatesDirectory, templatePath),
        filename: path.basename(templatePath),
      }
    })
    return result
  }

  protected _calculateFinalRelativeDestinationPathTemplate(
    currentTemplate: TemplatePathInfo,
  ): string {
    let finalRelativeDestinationPath = currentTemplate.relativePath
    const optionsValues = _.entries(this.options.prompting ?? {})
    optionsValues
      .filter(([, value]) => _.isString(value) && !_.isEmpty(value))
      .map(([key, value]) => {
        return { key: `$$${key}$$`, value: value }
      })
      .forEach(({ key, value }) => {
        if (finalRelativeDestinationPath.includes(key)) {
          finalRelativeDestinationPath = finalRelativeDestinationPath.replaceAll(
            key,
            value as string,
          )
        }
      })
    return finalRelativeDestinationPath
  }

  protected _resolveInternalGeneratorByAppType(appType: string): string {
    return require.resolve(
      path.resolve(__dirname, `../internal/generators/${appType}`),
    )
  }

  protected _resolveInternalPartial(partialGeneratorName: string): string {
    return require.resolve(
      path.resolve(__dirname, `../internal/partials/${partialGeneratorName}`),
    )
  }

  protected _capitalizeFirstLetter(string: string) {
    return string.charAt(0).toUpperCase() + string.slice(1)
  }

  protected _isSoapService(): boolean {
    const {
      prompting: { microserviceType, soapService },
    } = this.options
    return microserviceType === 'none' && !!soapService
  }

  protected _isCronGenerator(): boolean {
    const {
      prompting: { microserviceType, scheduledCron },
    } = this.options
    return microserviceType === 'none' && !!scheduledCron
  }

  private async _getAllRegularFilesFromDirectory(directory: string): Promise<Array<string>> {
    directory = path.resolve(directory)
    const result: Array<string> = []
    const existsDirectory = await this._existsPath(directory)
    if (existsDirectory) {
      const readdirTemplates = await fs.readdir(directory)
      for (const fileOrDirectory of readdirTemplates
        .filter((fileName) => !EXCLUDE_FILES_TO_WRITE.some((value) => fileName === value))
        .map((fileName) => path.resolve(directory, fileName))) {
        const stat = await fs.stat(fileOrDirectory)
        if (stat.isDirectory()) {
          result.push(...(await this._getAllRegularFilesFromDirectory(fileOrDirectory)))
        } else {
          result.push(fileOrDirectory)
        }
      }
    }
    return result
  }

  private async _existsPath(fileOrDirectory: string) {
    try {
      await fs.access(fileOrDirectory)
      return true
    } catch (e) {
      return false
    }
  }
}
