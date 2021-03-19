# RusbikMod

[![Tag Version](https://img.shields.io/github/v/tag/Kahzerx/RusbikMod.svg)](https://github.com/Kahzerx/RusbikMod/releases)
[![RusbikBuild status](https://github.com/Kahzerx/RusbikMod/actions/workflows/gradle.yml/badge.svg)](https://github.com/Kahzerx/RusbikMod/actions/workflows/gradle.yml)
[![License](https://img.shields.io/github/license/Kahzerx/RusbikMod.svg)](https://opensource.org/licenses/MIT)

Mod para la administración del server de subs de Rubik.


## Configuración
Al iniciar por primera vez con el mod instalado, se generará un archivo `RConfig.json` dentro de la carpeta del mundo, en él encontrarás información como chats permitidos, chat del chatbridge, lugar para la token, etc.

---

## Comandos

|Comando|Descripción|Privilegios|
|---|---|---|
|`/blockInfo`|Revisar el historial de cada bloque `/blockInfo x y z`.|`>0`|
|`/here`|Envía por el chat a todos los jugadores tu posición y aplica 5 segundos de glowing.|`>0`|
|`/home`|Tp al jugador a la posición `home`.|`>0`|
|`/pito`|¯\\\_(ツ)_/¯|`>0`|
|`/s`|Comando retirado del carpet para ponerse en survival, retira `Night Vision` y `Conduit Power`.| `>0`|
|`/sb`|Visualizar cualquier scoreboard.|`>0`|
|`/seed`|Habilitado para todos.|`>0`|
|`/setHome`|Registrar una posición como `home`.|`>0`|
|`/tp`|Tan solo tiene la función de hacer un tp de TU jugador a la posición de otro jugador (para que moderadores puedan asistir a otras personas sin abusar del `tp`).|`>1`|
|`/back`|Permite volver a la posición de tu última muerte.|`>2`|
|`/c`|Comando retirado del carpet para ponerse en spectator, añade `Night Vision` y `Conduit Power`.|`>2`|
|`/adminTp`|El comando tp vanilla.|`OP`|
|`/discord`|Seguido de `setBot <Token> <channelID>` para el chatbridge, usar `/discord start` y `/discord stop`.|`OP`|
|`/perms`|Aplicar un nivel de permisos a cualquier jugador conectado para tener disponible determinados comandos.|`OP`|
|`/randomCoords`|Hacer tp random al jugador en un radio de 10000 bloques desde 0 0.|`OP`|
|`/spoof`|Visualizar el contenido del ender chest/inventario(El inventario NO se actualiza correctamente y retirar y devolver un item al inventario resultará en la pérdida del mismo).|`OP`|

---

## Eventos

|Evento|Acción|
|---|---|
|Morir un jugador.|Notifiación por el chat de la posición de muerte.|
|Morir phantom por shulker.|40% de droppear elytras.|

---

## Discord

El bot realiza un check en cada autosave (cada 6000 ticks), de los members de discord que han abandonado el discord, o simplemente han dejado de ser suscriptores para sacarles de la whitelist.

Además de una `sincronización completa de whitelist` con base de datos para sacar a aquellos que ya estaban en la whitelist pero `no` han escrito `!add` o un administrador los ha registrado con `!exadd`.

La configuración se guarda en un archivo `RConfig.json` dentro de la carpeta del mundo.

|Comandos|Descripción|Categoría|
|---|---|---|
|`!online`|Lista jugadores conectados al servidor.|`allowedChat`|
|`!add <nombre>`|Añade al jugador a la whitelist y registra en la base de datos junto con ID de discord y aplica un rol, permite que al banear podamos evitar que ese ID de discord añada a nadie más, solo puedes añadir a un jugador a la whitelist.|`whitelistChat`|
|`!remove <nombre>`|Elimina al jugador nombrado, su ID de discord corresponde con el que añadió, tiene la finalidad de facilitar el cambio de cuentas de minecraft ya que solo puedes añadir a una persona.|`whitelistChat`|
|`!list`|Lista jugadores en whitelist.|`whitelistChat`|
|`!exadd <nombre>`|Añadir jugadores que no son subs a la whitelist, estos jugadores solo saldrán de la whitelist con el comando `!exremove`.|`adminChat`|
|`!exremove <nombre>`|Eliminar jugadores añadidos con el comando `!exadd`.|`adminChat`|
|`!ban <nombre>`|Banea al jugador del server de minecraft y bloquea que pueda añadir o eliminar a nadie de la whitelist.|`adminChat`|
|`!pardon <nombre>`|Desbanea al jugador del server de minecraft, se le devuelve el permiso de añadir o eliminar de whitelist.|`adminChat`|
|`!reload`|Hace un reload de whitelist además de re-sincronizar la información modificada en el archivo `RConfig.json`.|`adminChat`|

---

## Base de Datos

Este mod cuenta con una base de datos SQLite con 3 tablas (La base de datos se almacena en la carpeta `information/` dentro de la carpeta del mapa):
* player: `name, discordId, timesJoined, isBanned, perms`
* pos: `name, deathX, deathY, deathZ, deathDim, homeX, homeY, homeZ, homeDim`
* logger: `id, name, block, posX, posY, posZ, dim, action, date`

Relacionado con la `action` mencionado previamente:
* 0 = Break
* 1 = Place
* 2 = Use