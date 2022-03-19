# Spring Yeoman Generator

A [Yeoman](https://yeoman.io/) generator

## Installation

### Prerequisites
- Node >= 14
- NPM >= 6

### Commands for local install

```bash
git clone https://github.com/jsa4000/generator-spring
cd generator-spring
npm install --engine-strict
npm run install:local
```

### Commands for install from npm Registry

// TODO

### Common usage

And now, you can execute generator with this command:

```bash
npx yo spring [options]
```

or

```bash
npm install -g yo
yo spring [options]
```

## Getting Started

### Usage as a prompt

```bash
Usage:
  yo spring 

Output example: 
     _-----_
    |       |    ╭──────────────────────────╮
    |--(o)--|    │    Welcome to Spring     │
   `---------´   │        generator!        │
    ( _´U`_ )    ╰──────────────────────────╯
    /___A___\   /
     |  ~  |
   __'.___.'__
 ´   `  |° ´ Y `

? Select type of artifact in order to generate Microservice
? Enter the project name sample
? Select the strategy in order to develop this microservice API-First
? Would like to enable persistence layer? No
? Would you need invoke other microservices? No
? Would you need invoke WSDL/SOAP external web services? No
? Would like to enable Testcontainers framework testing? Yes
? Would like to enable Cucumber BDD framework testing? (experimental) No
```

### Usage as a CLI

```bash
Usage:
  yo spring --cli [options]

Options:
# Yeoman common parameters
  -h,   --help              # Print the generator's options and usage
        --skip-cache        # Do not remember prompt answers                Default: false
        --skip-install      # Do not automatically install dependencies     Default: false
        --force-install     # Fail on install dependencies error            Default: false
        --force             # Force overwrite files if any one exists       Default: false
        --ask-answered      # Show prompts for already configured options   Default: false
# Change output parameters
        --use-out-directory # Generator will use the ./out/${projectName} in order to deposite the result files
# Generator parameters
        --appType           # ¡Mandatory! Select type of artifact in order to generate. Available values: micro, soap-service, spi-service, cron
        --projectName       # Enter the project name. Default value: sample
        --microserviceType  # Microservice Type. ¡Mandatory if appType = micro!. Available values: apifirst, codefirst
        --persistenceLayer  # Enable persistence layer. Default value: false
        --feignClient       # Enable @FeignClient in order to invoke other microservices: Default value: false
        --jaxws             # Enable JAX-WS and add dependencies needed for invoke WSDL/SOAP external services. Default value: false
        --testcontainers    # Add dependencies and examples of integration testing with Testcontainer. Default value: true
        --cucumberTest      # Experimental option (only add Maven dependencies). Enable Cucumber. Default value: false
```