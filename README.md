# OmegaHouses
Housing plugin for Omega server

## Permissions
Permission Node | Description
--- | ---
omegahouses.create | Allows to create and delete houses
omegahouses.create.class | Allows to create house classes
omegahouses.forcesell | Allows to force sell a house
omegahouses.bypass | Allows to bypass chest and door protections

## Config
Parameter | Default Value | Description
--- | --- | ---
url | localhost | URL to your mysql server location
port | 3306 | The port your mysql server is using
database | database | The name of the database you want to use
user | username | The username to acces the database with
pass | password | The password of that user
close_doors | true | If set to true, the main door will be closed after a predefined time
close_doors_delay | 2 | Delay in seconds to auto close doors
house_limit | 1 | Number of houses a player can have