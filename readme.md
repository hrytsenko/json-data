[![Build Status](https://travis-ci.org/hrytsenko/json-data.svg?branch=master)](https://travis-ci.org/hrytsenko/json-data)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=hrytsenko_json-data&metric=alert_status)](https://sonarcloud.io/dashboard?id=hrytsenko_json-data)
[![](https://jitpack.io/v/hrytsenko/json-data.svg)](https://jitpack.io/#hrytsenko/json-data)

# JSON data

JSON data uses a dynamic representation of data objects and domain-specific languages for data manipulations.
The main trade-off of this approach is additional costs to represent and manipulate data objects.

## Navigation

JSON data uses [JsonPath] DSL to navigate over documents.
This DSL uses expressions to express a path in a document.

The following example illustrates the expression that refers an entity name:

```
Input:
{
  "entity": {
    "name":"ENTITY"
  }
}
 
Expression:
$.entity.name
 
Output:
"ENTITY"
``` 

## Transformation

JSON data use [Jolt] DSL to transform documents.
This DSL uses specifications to express a list of chained transformations.
Each transformation consumes an output document from a previous transformation and produces an input document for a next transformation.

The major transformations are _shift_ and _default_.
The _shift_ transformation maps content from an input document to an output document.
This transformation supports iterators, conditions and wildcards that allow to implement a complex logic.
The _default_ transformation adds content to an output document.
This transformation ensures completeness of an output document.

The following example illustrates the specification that creates a collection from a single entity:

```
Input:
{
  "entity": {
    "name": "ENTITY"
  }
}
 
Specification:
[
  {
    "operation": "shift",
    "spec": {
      "entity": "entities[].entity"
    }
  },
  {
    "operation": "default",
    "spec": {
      "entities": []
    }
  }
]
 
Output:
{
  "entities": [
    {
      "entity": {
        "name": "ENTITY"
      }
    }
  ]
}
```

## Validation

JSON data use [Justify] DSL (namely JSON Schema) to validate documents.
This DSL uses schemas to express a structure and a content of a document.

The following example illustrates the schema that validates a single entity:

```
Input:
{
  "entity": {
    "name": "ENTITY"
  }
}
 
Schema:
{
  "type": "object",
  "properties": {
    "entity": {
      "type": "object",
      "properties": {
        "name": {
          "type": "string",
          "pattern": "^.{5,30}$"
        }
      },
      "additionalProperties": false,
      "required": [
        "name"
      ]
    }
  },
  "additionalProperties": false,
  "required": [
    "entity"
  ]
}
```

## Dependencies

This library has the following dependencies:

| Library    | License      | Purpose        |
|------------|--------------|----------------|
| [Jackson]  | [Apache 2.0] | Serialization  |
| [JsonPath] | [Apache 2.0] | Navigation     |
| [Jolt]     | [Apache 2.0] | Transformation |
| [Justify]  | [Apache 2.0] | Validation     |

[JsonPath]: https://github.com/json-path/JsonPath
[Jackson]: https://github.com/FasterXML/jackson-databind
[Justify]: https://github.com/leadpony/justify
[Jolt]: https://github.com/bazaarvoice/jolt

[Apache 2.0]: https://www.apache.org/licenses/LICENSE-2.0
