import json
import sqlite3


def insert(name, perms, x, y, z, dim):
    cursorObj = con.cursor()
    cursorObj.execute(f"INSERT INTO player (name,perms,homeX,homeY,homeZ,homeDim) VALUES ('{name}',{perms},{x},{y},{z},'{dim}')")
    con.commit()


con = sqlite3.connect('server.db')

with open('server.json', 'r') as jsonFile:
    data = jsonFile.read()
    for line in json.loads(data):
        if line['player']['home']['x'] != '':
            insert(line['player']['name'], 1, line['player']['home']['x'], line['player']['home']['y'], line['player']['home']['z'], line['player']['home']['dim'])