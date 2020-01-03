#!/usr/bin/env python
# -*- coding: utf-8 -*-
import WazeRouteCalculator
import logging

from flask import Flask
from flask import request
app = Flask(__name__)

from_address = 'Ashqelon, Israel'
to_address = 'Ashdod, Israel'


@app.route('/', methods=['GET', 'POST'])
def user_function():
    if request.method == 'GET':
        """return the information for <user_id>"""
        return "{""hello"":1}"
    if request.method == 'POST':
        """modify/update the information for <user_id>"""
        # you can use <user_id>, which is a str but could
        # changed to be int or whatever you want, along
        # with your lxml knowledge to make the required
        # changes
        from_address = request.form # a multidict containing POST data
    else:
        # POST Error 405 Method Not Allowed
        return None


logger = logging.getLogger('WazeRouteCalculator.WazeRouteCalculator')
logger.setLevel(logging.DEBUG)
handler = logging.StreamHandler()
logger.addHandler(handler)

route = WazeRouteCalculator.WazeRouteCalculator(from_address, to_address)

try:
    route.calc_route_info()
    app.run(host='192.168.137.44')
except WazeRouteCalculator.WRCError as err:
    print(err)
