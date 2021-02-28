#!/usr/bin/python3

from time import sleep, gmtime, strftime
import ftplib
import socket
import requests
import os
import shutil
from bs4 import BeautifulSoup
from dotenv import load_dotenv

headers = {
    'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36'}

def deleteIfExists():
    print('Checking if zip file already exists...')
    filename = 'world.zip'
    ftp = ftplib.FTP( os.getenv('IP'))
    ftp.login(user=f'{os.getenv("USER")}.218', passwd=os.getenv('PASSWD'))

    if filename in ftp.nlst():
        print('Zip file already exists, deleting...')
        ftp.delete(filename)
        sleep(3)

    ftp.quit()

def makeBackup():
    with requests.Session() as s:
        r = s.get(os.getenv('LOGINPATH'), headers=headers)
        token = BeautifulSoup(r.text, 'lxml').find(
            'input', attrs={'name': 'YII_CSRF_TOKEN'})['value']

        login = {
            'LoginForm[name]': os.getenv('USER'),
            'LoginForm[password]': os.getenv('PASSWD'),
            'YII_CSRF_TOKEN': token
        }

        s.post(os.getenv('LOGINPATH'), data=login)

        command = {
            'ajax': 'command',
            'command': 'builtin:backup',
            'YII_CSRF_TOKEN': token
        }

        s.post(os.getenv('CHATURL'), data=command)
        print('Making backup...')

def wait():
    print('Waiting for the backup to finish...')
    exists = False
    while not exists:
        filename = 'world.zip'
        ftp = ftplib.FTP(os.getenv('IP'))
        ftp.login(user=f'{os.getenv("USER")}.218', passwd=os.getenv('PASSWD'))
        if filename in ftp.nlst():
            exists = True
        ftp.quit()
        sleep(60)
    print('Backup done :D')

def FTPStuff():
    print('Downloading file...')
    filename = 'world.zip'
    ftp = ftplib.FTP(os.getenv('IP'))
    ftp.login(user=f'{os.getenv("USER")}.218', passwd=os.getenv('PASSWD'))

    ftpfile = open(filename, 'wb')
    ftp.retrbinary("RETR " + filename, ftpfile.write, 1024)
    ftp.delete(filename)
    ftp.quit()
    ftpfile.close()
    print('Done :D')

def move(date):
    print('Moving file...')
    shutil.move('world.zip', f'/var/HDD/nextcloud/kahzerx/files/Rubik/{date}.zip')
    os.system(f'chown -R http:http /var/HDD/nextcloud/kahzerx/files/Rubik/{date}.zip')
    os.system('sudo -u http php /usr/share/webapps/nextcloud/occ files:scan --path="kahzerx/files/Rubik"')

if __name__ == "__main__":
    load_dotenv()
    deleteIfExists()
    makeBackup()
    wait()
    date = strftime("%d-%m-%Y", gmtime())
    FTPStuff()
    move(date)