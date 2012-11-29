bamboo-traffic-light-plugin
===========================

Atlassian Bamboo plugin for toy traffic light.

Presents simple TCP server for managing toy traffic light. Listening port is not configurable, it's 9090.

Settings
--------
User may set traffic light program (text message) for every plan. See sections 'Pre Build Traffic Light Program' and 'Post Build Traffic Light Program' in job configuration tab 'Miscellaneous'.

Protocol
--------
After connecting to port 9090 user must send plan name followed by '\n'. Server disconnects client with unknown plan names.

When plan build starts all listening subscribers receive pre-build program followed with '\n'.
When plan build finishes (successful or failure) all listening subscribers receive appropriate post-build program followed with '\n'.
