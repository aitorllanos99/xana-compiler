# Laboratorio 13

## Objetivos

Finalmente, en este laboratorio implementaremos:

- Invocation (las definiciones de funciones ya han sido implementadas en el [lab 11](lab_11.md))
- Return

## Instrucciones

Existen dos programas de prueba:

- [`codegeneration/complete/input.xana`](../../examples/codegeneration/complete/input.xana)
- [`codegeneration/complete/big_input.xana`](../../examples/codegeneration/complete/big_input.xana)

> ⚠ **Importante:** Tu compilador debe ser capaz de compilar estos dos ficheros (no pueden ser modificados) y debes traer estos dos ficheros al examen. El código generado debe poder ejecutarse en MAPL sin errores ni warnings.

Genera el código MAPL ejecutando:

```
mvn clean compile exec:java -Dexec.mainClass="es.uniovi.dlp.commandline.CLI" -Dexec.args="examples/codegeneration/complete/input.xana"
```

Recuerda que puedes probar el programa usando:

```
.\mapl\TextVM.exe examples/codegeneration/complete/input.xana.mp
```

Ahora también puedes compilar e interpretar el programa en un mismo comando:
```
.\bin\xana.bat examples/codegeneration/complete/input.xana
```

En macOS y Linux:

```
bin/xana.sh examples/codegeneration/complete/input.xana
```