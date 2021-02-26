#!/usr/bin/python3

from time import sleep
import socket
import requests
from dotenv import load_dotenv
from bs4 import BeautifulSoup
import os

headers = {
    'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36'}


def restartServer():
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
            'command': 'start',
            'YII_CSRF_TOKEN': token
        }

        s.post(os.getenv('CHATURL'), data=command)


def mainLoop():
    isDown = False
    while True:
        try:
            if isDown:
                print('restarting...')
                restartServer()
                isDown = False
                sleep(30)
            sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            result = sock.connect_ex((os.getenv('IP'), int(os.getenv('PORT'))))
            isDown = result != 0
            sleep(10)
        except KeyboardInterrupt:
            print("bai baaai~")
            exit()
        except:
            print("Uh ooooh, is the host down?")
            sleep(5)


if __name__ == "__main__":
    load_dotenv()
    mainLoop()

