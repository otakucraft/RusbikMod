# RusbikMod
Mod para la administración del server de subs de Rubik.

Este mod cuenta con una base de datos SQLite con 3 tablas:
* player: name, discordId, timesJoined, isBanned, perms
* pos: name, deathX, deathY, deathZ, deathDim, homeX, homeY, homeZ, homeDim
* logger: id, name, block, posX, posY, posZ, dim, action, date

Relacionado con la `action` mencionado previamente:
* 0 = Break
* 1 = place
* 2 = Use

## Configuración
Al iniciar por primera vez con el mod instalado, se generará un archivo `config.yaml`, en él encontrarás información como chats permitidos, chat del chatbridge, lugar para la token, etc.

# Funciones

## AdminTeleport
Con la finalidad de que Moderadores (personas con privilegios de nivel 2), sean capaces de atender a personas en caso de que administradores se encuentren ausentes, se ha decidido mover el comando tp de vanilla a `/adminTp`, solo disponible para jugadores con OP, y crear un nuevo comando `/tp`.
* Comando: `/adminTp`
* Privilegios: `OP`

## tp
Comando al que únicamente personas con privilegios de nivel 2 puede acceder, y que tan solo tiene la función de hacer un tp de tu jugador a la posición de otro jugador.
* Comando: `/tp`
* Privilegios: `>1`

## back
Permite volver a la posición de tu última muerte, no implementado, solo disponible para nivel de privilegios 3.
* Comando: `/back`
* Privilegios: `>2`

## blockInfo
Revisar el historial de cada bloque `/blockInfo x y z`, cuenta con sistema de páginas.
* Comando: `/blockInfo`
* Privilegios: `null`

## discord
Vinculación con un chat de discord para la gestión de la whitelist, chatbridge, etc.
* Comando: `/discord (setBot,start,stop) token channelID(para el chatbridge)`
* Privilegios: `OP`

La configuración se guarda en un archivo `config.yml`.
Comandos disponibles para el bot de discord:
* `!online`: funciona en los chats dentro de la categoría `allowedChat`, lista jugadores conectados al servidor.
* `!add`: funciona en los chats dentro de la categoría `whitelistChat`, añade un solo jugador a la whitelist, además de registrarlo en la base de datos junto con su id de discord, y aplicar un rol.
* `!remove`: funciona en los chats dentro de la categoría `whitelistChat`, elimina al jugador nombrado, solo si corresponde con el registrado anteriormente, tiene la finalidad de facilitar el cambio de cuentas de minecraft.
* `!list`: funciona en los chats dentro de la categoría `whitelistChat`, lista jugadores en whitelist.
* `!exadd`: funciona en los chats dentro de la categoría `adminChat`, puedes añadir jugadores que no son subs a la whitelist, estos jugadores solo saldrán de la whitelist con el comando !exremove.
* `!exremove`: funciona en los chats dentro de la categoría `adminChat`, eliminar jugadores añadidos con el comando !exadd.
* `!ban`: funciona en los chats dentro de la categoría `adminChat`, banea al jugador del server de minecraft y bloquea que pueda añadir o eliminar a nadie de la whitelist.
* `!pardon`: funciona en los chats dentro de la categoría `adminChat`, desbanea al jugador del server de minecraft, se le devuelve el permiso de añadir o eliminar de whitelist.
* `!reload`: funciona en los chats dentro de la categoría `adminChat`, hace un reload además de re sincronizar la información modificada en el archivo `config.yml`.

## here
Envía por el chat a todos los jugadores tu posición, así como una traducción a coordenadas del overworld o nether dependiendo de la dimensión, y aplica 5 segundos de glowing.
* Comando: `/here`
* Privilegios: `null`

## home
Registrar una posición como home, al reescribir el comando se sobre-escribirá la posición antigua.
* Comando: `/setHome`
* Privilegios: `0`

Hará tp al jugador a la posición previamente configurada por medio del comando `/setHome`.
* Comando: `/home`
* Privilegios: `0`

## randomTp
Hacer tp random al jugador en un radio de 10000 bloques con la finalidad de alejar del spawn y evitar griefing (no funciona BTW, los aleja pero la gente se junta y al final es inutil...), solo disponible para OP.
* Comando: `/randomCoords`
* Privilegios: `OP`

## camera & survival
Comandos retirados del carpet para alternar entre spectator y survival, al igual que el comando `/back`, no está implementado, requiere de privilegios de nivel 3.
* Comando: `/c`
* Privilegios: `>2`

* Comando: `/s`
* Privilegios: `null`

## scoreboard
Todos los usuarios pueden visualizar cualquier scoreboard.
* Comando: `/sb`
* Privilegios: `null`

## spoof
Visualizar el contenido del ender chest de cualquier jugador conectado, únicamente disponible para OP.
* Comando: `/spoof`
* Privilegios: `OP`

## perms
Aplicar un nivel de permisos a cualquier jugador conectado para tener disponible determinados comandos, solo disponible para jugadores con OP.
* Comando: `/perms`
* Privilegios: `OP`

## otros
* Al morir se envía al jugador un mensaje con la posición de muerte y dimensión.
* Jugadores añadidos manualmente a la whitelist no podrán ejecutar ningún comando con privilegios distinto de `null`, al no tener un registro en la base de datos.
* `/seed` está habilitado para todos los jugadores independientemente del OP.
* `/pito` ¯\\\_(ツ)_/¯
