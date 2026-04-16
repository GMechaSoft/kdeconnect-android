A continuación, presento la transcripción técnica de las políticas de KDE Connect, estructurada como un conjunto de reglas de sistema para que un modelo de lenguaje las procese como contexto operativo:
## 1. Política de Mensajes de Commit (Git)

* Formato de Asunto: Debe ser una línea única de máximo 72 caracteres. No debe terminar con un punto final.
* Cuerpo del Mensaje: Obligatorio si el cambio no es trivial. Debe separarse del asunto por una línea en blanco.
* Contenido: Debe explicar el "por qué" del cambio, no solo el "qué".
* Restricciones: No utilizar emojis en ninguna parte del mensaje.
* Vinculación: Si el commit resuelve un reporte, debe incluir la etiqueta BUG: [número] o CCBUG: [número] al final.

## 2. Estándares de Código y Calidad

* Compilación: El código debe compilar sin errores en la rama actual (master) antes de ser enviado.
* Estilo de Programación: Debe imitar estrictamente el estilo existente en el archivo que se está editando (indentación, uso de llaves, espacios).
* Convenciones de KDE:
* Seguir las reglas de nomenclatura de KDE (usualmente camelCase para variables y métodos).
   * Respetar el orden de los encabezados (#include) según las guías de KDE Frameworks.
* Mantenibilidad: El código debe ser limpio y estar mínimamente comentado donde la lógica sea compleja.

## 3. Protocolo para Nuevas Funcionalidades

* Validación Previa: Antes de programar, la idea debe discutirse en los canales oficiales (Matrix o lista de correo) para asegurar que encaja con la visión del proyecto.
* Flujo de Trabajo:
* Uso mandatorio de Merge Requests (MR) a través de la instancia GitLab de KDE.
   * Las ramas de trabajo deben tener el prefijo work/.
* Modularidad (Plugins): Las nuevas funciones deben implementarse como "plugins" independientes siempre que sea posible, permitiendo al usuario activarlas o desactivarlas.
* Compatibilidad del Protocolo: Si la función requiere comunicación entre dispositivos, se debe definir el paquete JSON siguiendo la estructura del protocolo central de KDE Connect para garantizar la interoperabilidad multiplataforma.

## 4. Políticas de Seguridad y Privacidad (Core)

* Privacidad por Diseño: Está prohibido el envío de datos a servidores externos o terceros. Toda comunicación debe ser estrictamente local (LAN).
* Cifrado: Cualquier nueva implementación de red debe utilizar obligatoriamente la capa de cifrado TLS establecida por el protocolo.
* Consentimiento: Si la funcionalidad accede a datos sensibles (contactos, SMS, archivos), debe solicitar permisos de Android de forma explícita y permitir su revocación granular.

¿Deseas que utilice este contexto para revisar un borrador de código o para redactar un mensaje de commit que cumpla con estas reglas?

