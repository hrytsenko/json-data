[![Build Status](https://travis-ci.org/hrytsenko/json-data.svg?branch=master)](https://travis-ci.org/hrytsenko/json-data)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=hrytsenko_json-data&metric=alert_status)](https://sonarcloud.io/dashboard?id=hrytsenko_json-data)
[![](https://jitpack.io/v/hrytsenko/json-data.svg)](https://jitpack.io/#hrytsenko/json-data)

# Summary

This library enables a JSON data model that substitutes a traditional POJO model.
JSON data shifts data objects manipulation from Java to domain-specific languages.
These languages are declarative and express manipulations via expressions, specifications and schemas.

The main trade-off of this model is additional costs to manipulate data objects.
However, this trade-off does not have decisive influence on performance in distributed systems, except cases where an extremely low latency is a key requirement.

# Representation

## Entities

JSON data encourages simple and concise data models.

The following example illustrates a simple JSON entity:

```java
public class Entity extends JsonEntity<Entity> {

  public String getName() {
    return getString("entity.name");
  }

} 
```

Note that JSON entity class must define a public no-argument constructor (or use a default constructor).

## Documents

JSON data encourages self-descriptive and context-independent documents.

The following example illustrates a document that contains a single entity:

```json
{
  "entity": {
    "name": "ENTITY"
  }
}
```

The following example illustrates a document that contains a collection of entities:

```json
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

The following example illustrates a document that contains a tuple of entities:

```json
{
  "entity": {
    "name": "ENTITY"
  },
  "event": {
    "name": "EVENT"
  }
}
```

# Manipulation

## Navigation

JSON data use [JsonPath] DSL to navigate over documents.
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

The major transformation are _shift_ and _default_.
The _shift_ transformation maps content from an input document to an output document.
This transformation supports iterators, conditions and wildcards that allow to implement complex logic.
The _default_ transformation adds content to an output document.
This transformation allows to ensure completeness of an output document.

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

This schema is strict, because it allows to determine a maximum size of a JSON document.
Such schemas forbid additional properties for all objects and define a maximum length for all arrays, strings and dynamic properties names.

# Dependencies

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

[JitPack]: https://jitpack.io/
