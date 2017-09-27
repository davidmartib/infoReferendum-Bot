**infoReferendumBot**  

Bot de Telegram que permet consultar la informació del centre de votació assignat pel referendum de l'1 d'Octubre de 2017 a Catalunya.

Aquest projecte ha estat creat amb IntelliJ IDEA i te dependencies de:  

- Apache log4j2 (https://logging.apache.org/log4j/2.x/)
- Telegrambots (https://github.com/rubenlagus/TelegramBots)

Està basat en els projectes:

- https://github.com/onvotar-bot/onvotar-whatsapp
- https://github.com/referendum1oct/referendum1oct.github.io

Per funcionar, necessita la base de dades encriptada que es pot descarregar desde https://github.com/onvotar-bot/onvotar-base  

També és necessari tenir instal·lat el runtime de NodeJS, que és a on és resol la desencriptació de les dades.

Es necessari també un fitxer de configuració que es digui referendumBot.ini amb el següent contingut:

dirDB=_Directori on està la base de dades (carpeta db)_  
nodejsCmd=_Ruta complerta al executable de nodeJS_  
nomBot=_Nom del Bot_  
tokenBot=_Token d'autenticació del Bot, obtinguda al crear-lo_  
