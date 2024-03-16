import re

class Token:
    def __init__(self, type_, value):
        self.type = type_
        self.value = value