import { Answers } from 'yeoman-environment'
import { GeneratorOptions, Question } from 'yeoman-generator'

export interface BaseGeneratorOptions extends GeneratorOptions {

    questions?: Array<Question<Answers> & {
        /**
         * The choices of the prompt.
         */
        choices?: any;
        default?: any;
    }>

}