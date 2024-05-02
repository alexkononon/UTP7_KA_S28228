# Simulation Models User Guide

## Introduction

This user guide describes how to use the program for conducting simulations on economic models and utilizing scripts for data analysis. The program allows for defining simulation models, managing computations, and analyzing results.

## Environment Setup

To use the program, you need to have the following environments installed:

- Java Development Kit (JDK) version 8 or higher.
- Groovy environment if you plan to write scripts in this language.

## Project Structure

The project consists of the following main components:

1. **Model Classes**: Classes containing simulation models, where model variables and methods for performing computations are defined.
2. **Controller Class**: Manages computations, reads input data, runs models, and executes scripts.
3. **Scripts**: Optional Groovy scripts for further data analysis.
4. **Graphical Interface**: Graphical user interface for handling models and scripts. Swing was used.

## Using the Program

1. **Defining Models**: Define simulation models by creating classes with appropriate fields and a `run()` method.
2. **Loading Input Data**: Prepare input files where each line contains values for model variables and a specification of computation years.
3. **Running Models**: Using the Controller class, load input data and run models using the `runModel()` method. (Made in GUI implementation).
4. **Executing Scripts**: Optionally, use Groovy scripts for further data analysis. Scripts can be executed using the `runScript()` or `runScriptFromFile()` methods.
5. **Analyzing Results**: Obtain computation results and data from scripts in text format thanks to `getResultsAsTsv()` method and GUI representation.

## Launching Example

An example of the model is in the program itself in the models directory.
In the task, annotations labeled with @Bind mark fields in the model class, ensuring their accessibility across the program. 
This simplifies communication between different components. Annotations specify special behaviors for these fields, such as binding input data or calculation results. 
By utilizing annotations, the codebase becomes more modular and promotes clarity in the system's structure and interactions.

As script1.groovy will use the following code block.
```Groovy
ZDEKS = new double[LL]
for (i = 0; i < LL; i++) {
    ZDEKS[i] =  EKS[i]/PKB[i];
}
```
As a data1.txt the following text will be used.
```
LATA	2015	2016	2017	2018	2019
twKI	1.03
twKS	1.04
twINW	1.12
twEKS	1.13
twIMP	1.14
KI	1023752.2
KS	315397
INW	348358
EKS	811108.6
IMP	784342.4
```

The files need to be placed in Modeling/data and Modeling/scripts, which are located in the usеr.home directory

### Step by step operation of the program.


The user's window after launching the program:
<img width="1012" alt="Screenshot 2024-05-02 at 23 20 38" src="https://github.com/alexkononon/UTP7_KA_S28228/assets/117831770/6fcbb519-2b6d-44e4-bd2c-53b9afc284c8">

The table is presented as a result of model selection and data collection:
<img width="1012" alt="Screenshot 2024-05-02 at 23 20 46" src="https://github.com/alexkononon/UTP7_KA_S28228/assets/117831770/48d4aa97-d597-44cd-9ad3-6b4530cf5bef">

The program provides the ability to run a script, such as Groovy. (ScriptEngine technology is used)
<img width="662" alt="Screenshot 2024-05-02 at 23 21 09" src="https://github.com/alexkononon/UTP7_KA_S28228/assets/117831770/f3f49e4d-ded0-47eb-933b-f066ffac6f69">

The result after running the script:
<img width="1012" alt="Screenshot 2024-05-02 at 23 21 16" src="https://github.com/alexkononon/UTP7_KA_S28228/assets/117831770/14bda8f9-f075-4b0a-a85b-113000fce19e">

It is also possible to run a script written in the application itself, and not as a file:
<img width="638" alt="Screenshot 2024-05-02 at 23 34 14" src="https://github.com/alexkononon/UTP7_KA_S28228/assets/117831770/4502f48c-4ef6-4728-9142-42d0949bd146">

The result after running the script:
<img width="1012" alt="Screenshot 2024-05-02 at 23 34 18" src="https://github.com/alexkononon/UTP7_KA_S28228/assets/117831770/1645b25d-eefa-4d5d-b5c4-1ce7cdd0a1dc">
