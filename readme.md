[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=hrytsenko_json-data&metric=alert_status)](https://sonarcloud.io/dashboard?id=hrytsenko_json-data)
[![](https://jitpack.io/v/hrytsenko/json-data.svg)](https://jitpack.io/#hrytsenko/json-data)

# JSON data

This library allows to manipulate JSON entities without serialization into POJOs.
It encourages use of DSLs to manipulate, transform and validate JSON objects.

The JSON entity inherits the class `JsonEntity` and defines methods to manipulate the underlying object:

```java
class Entity extends JsonEntity<Entity> {

  String getName() {
    return getString("entity.name");
  }

}
```

Besides, the class `JsonBean` represents the generic JSON entity for temporary objects with the limited scope.

## Serialization

This library uses [Jackson] to serialize JSON entities.
The class `JsonParser` provides the serialization API.

## Manipulation

This library uses [JsonPath] DSL to manipulate JSON entities.
The class `JsonEntity` provides the manipulation API.

The [JsonPath] DSL uses expressions to navigate JSON.
The following example illustrates the DSL expression that refers an entity name:

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

This library uses [Jolt] DSL to transform JSON entities.
The class `JsonMapper` provides the transformation API.

The [Jolt] DSL uses specifications to express a list of chained transformations.
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

Each transformation consumes an output document from a previous transformation and produces an input document for a next transformation.
The major transformations are _shift_ and _default_.
The _shift_ transformation maps content from an input document to an output document.
It supports iterators, conditions and wildcards that allow to implement a complex logic.
The _default_ transformation adds content to an output document.
It ensures completeness of an output document.

## Validation

This library uses [Justify] DSL (namely JSON Schema) to validate JSON entities.
The class `JsonValidator` provides the validation API.

The [Justify] DSL uses schemas to express a structure and a content of a document.
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

[Jackson]: https://github.com/FasterXML/jackson-databind
[JsonPath]: https://github.com/json-path/JsonPath
[Jolt]: https://github.com/bazaarvoice/jolt
[Justify]: https://github.com/leadpony/justify
