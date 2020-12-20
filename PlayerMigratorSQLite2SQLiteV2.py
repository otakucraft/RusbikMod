import sqlite3

old = sqlite3.connect('old.db')
new = sqlite3.connect('server.db')

if __name__ == "__main__":
    oldCursor = old.cursor()
    newCursor = new.cursor()

    oldCursor.execute('''SELECT * FROM player''')
    result = oldCursor.fetchall()
    for res in result:
        print(res)
        userId = input(f'userId de {res[0]}?: ')
        newCursor.execute(f"INSERT INTO player (name, discordId, timesJoined, isBanned, perms) VALUES ('{res[0]}', {int(userId)}, 0, 0, {res[1]});")
        new.commit()
        null = 'NULL'
        newCursor.execute(f"INSERT INTO pos (name, deathX, deathY, deathZ, deathDim, homeX, homeY, homeZ, homeDim) VALUES ('{res[0] if res[0] is not None else null}', {res[2] if res[2] is not None else null}, {res[3] if res[3] is not None else null}, {res[4] if res[4] is not None else null}, '{res[5] if res[5] is not None else null}', {res[6] if res[6] is not None else null}, {res[7] if res[7] is not None else null}, {res[8] if res[8] is not None else null}, '{res[9] if res[9] is not None else null}');")
        new.commit()
    
    newCursor.execute('''SELECT * FROM player''')
    result = newCursor.fetchall()
    for res in result:
        print(res)
    
    newCursor.execute('''SELECT * FROM pos''')
    result = newCursor.fetchall()
    for res in result:
        print(res)
