import Scanner
from Scanner import CustomError

from Scanner import Tokens

RESERVED_KEYWORDS = ['fn','where', 'let', 'aug', 'within' ,'in' ,'rec' ,'eq','gr','ge','ls','le','ne','or','@','not','&','true','false','nil','dummy','and','|']

class ASTNode:

    def __init__(self, type):
        self.type = type
        self.value = None
        self.sourceLineNumber = -1
        self.child = None
        self.sibling = None
        self.indentation = 0

    def print_tree(self):
        #print(self.type)

        if self.child:
            self.child.print_tree()

        if self.sibling:
            self.sibling.print_tree()


    # output to the file
    def print_tree_to_file(self, file):

        for i in range(self.indentation):
            file.write(".")
        # if(self.type ==)
        file.write(str(self.type) + "\n")

        if self.child:

            self.child.indentation = self.indentation + 1
            self.child.print_tree_to_file(file)

        if self.sibling:
            self.sibling.indentation = self.indentation
            self.sibling.print_tree_to_file(file)
class TreeNode:
    def __init__(self, data):
        self.data = data
        self.children = []
        self.parent = None

    def add_child(self, child):
        child.parent = self
        self.children.append(child)

    # pre-order traversal of n araay tree
    def print_tree(self):
        #print(self.data)
        if self.children:
            for child in self.children:
                child.print_tree()


class ASTParser:

    def __int__(self, tokens1):
        self.tokens = tokens1
        self.current_token = None
        self.index = 0

    def read(self):

        if self.current_token.type == ('<IDENTIFIER>' or '<INTEGER>' or '<STRING>') :

            terminalNode = ASTNode( "<"+str(self.current_token.type)+":"+  str(self.current_token.value)+">")
            stack.append(terminalNode)
        if self.current_token.value in  ['true', 'false', 'nil', 'dummy']:
            stack.append(ASTNode(self.current_token.value))

        self.index += 1

        if (self.index < len(self.tokens)):
            self.current_token = self.tokens[self.index]

    def buildTree(self, token, ariness):
        global stack

        '''for node in stack:
            print(node.type)'''

        node = ASTNode(token)
        node.value = None
        node.sourceLineNumber = -1
        node.child = None
        node.sibling = None

        while ariness > 0:
            child = stack[-1]
            stack.pop()
            # Assuming pop() is a function that returns an ASTNode
            if node.child is not None:
                child.sibling = node.child
            node.child = child
            node.sourceLineNumber = child.sourceLineNumber
            ariness -= 1

        node.print_tree()

        stack.append(node)  # Assuming push() is a function that pushes a node onto a stack
        '''for node in stack:
            print(node.type)'''

    def E(self):

        if self.current_token.value == 'let':
            self.read()
            self.D()

            if self.current_token.value != 'in':
                print('current token instead of in:' , self.current_token.value)
                raise CustomError('in is expected')

            self.read()
            self.E()
            #print('E->let D in E')
            self.buildTree("let", 2)

        elif self.current_token.value == 'fn':

            n = 1

            self.read()
            self.Vb()

            while self.current_token.type == '<IDENTIFIER>' or self.current_token.value == '(':
                self.Vb()
                n += 1

            if self.current_token.value != '.':
                raise CustomError("Error: . is expected")

            self.read()
            self.E()
            #print('E->fn Vb . E')
            self.buildTree("lambda", n)

        else:
            self.Ew()
            #print('E->Ew')

    def Ew(self):

        self.T()
        #print('Ew->T')

        if self.current_token.value == 'where':
            self.read()
            self.Dr()
            #print('Ew->T where Dr')
            self.buildTree("where", 2)

    def T(self):
        self.Ta()
        # print('T->Ta')

        n = 1
        while self.current_token.value == ',':
            self.read()
            self.Ta()
            n += 1
            #print('T->Ta , Ta')
        self.buildTree("tau", n)

    def Ta(self):
        self.Tc()
        #print('Ta->Tc')
        while self.current_token.value == 'aug':
            self.read()
            self.Tc()
            #print('Ta->Tc aug Tc')

            self.buildTree("aug", 2)

    def Tc(self):

        self.B()
        #print('Tc->B')
        if self.current_token.type == '->':
            self.read()
            self.Tc()

            if self.current_token.value != '|':
                raise CustomError("Error: | is expected")
            self.read()
            self.Tc()
            #print('Tc->B -> Tc | Tc')
            self.buildTree("->", 3)

    def B(self):

        self.Bt()
        #print('B->Bt')
        while self.current_token.value == 'or':
            self.read()
            self.Bt()
            #print('B->Bt or Bt')
            self.buildTree("or", 2)

    def Bt(self):

        self.Bs()
        #print('Bt->Bs')
        while self.current_token.value == '&':
            self.read()
            self.Bs()
            #print('Bt->Bs & Bs')
            self.buildTree("&", 2)

    def Bs(self):

        if self.current_token.value == 'not':
            self.read()
            self.Bp()
            #print('Bs->not Bp')
            self.buildTree("not", 1)
        else:
            self.Bp()
            #print('Bs->Bp')

    def Bp(self):

        self.A()
        #print('Bp->A')

        ##  Bp -> A ( 'gr' | '>') A
        if self.current_token.value == '>'or self.current_token.value == 'gr' :
            self.read()
            self.A()
            #print('Bp->A (gr|>) A')
            self.buildTree("gr", 2)

        elif self.current_token.value == '>=' or self.current_token.value == 'ge':
            self.read()
            self.A()
            #print('Bp->A (ge|>=) A')
            self.buildTree("ge", 2)

        elif self.current_token.value == '<' or  self.current_token.value == 'ls':
            self.read()
            self.A()
            #print('Bp->A (ls| <) A')
            self.buildTree("ls", 2)

        elif self.current_token.value == '<=' or self.current_token.value == 'le':
            self.read()
            self.A()
            #print('Bp->A (le| <=) A')
            self.buildTree("le", 2)

        elif self.current_token.value == 'eq':
            self.read()
            self.A()
            #print('Bp->A eq A')
            self.buildTree("eq", 2)

        elif self.current_token.value == 'ne':
            self.read()
            self.A()
            #print('Bp->A ne A')
            self.buildTree("ne", 2)


    def A(self):

        if self.current_token.value == '+':
            self.read()
            self.At()
            #print('A->+ At')

        elif self.current_token.value == '-':
            self.read()
            self.At()
            #print('A->- At')
            self.buildTree("neg", 1)


        else:
            self.At()
            #print('A->At')
        while self.current_token.value == '+' or self.current_token.value == '-':

            sign = self.current_token.value

            self.read()
            self.At()
            #print('A->At + / - At')
            self.buildTree(sign, 2)


    def At(self):
        #print('procAt')

        self.Af()
        #print('At->Af')
        while self.current_token.value == '*' or self.current_token.value == '/':
            self.read()
            self.Af()
            #print('At->Af *|/ Af')
            self.buildTree(self.current_token.value, 2)

    def Af(self):
        #print('procAf')

        self.Ap()
        #print('Af->Ap')
        if self.current_token.value == '**':
            self.read()
            self.Af()
            #print('Af->Ap ** Af')
            self.buildTree("**", 2)

    def Ap(self):
        #print('procAp')

        self.R()
        #print('Ap->R')
        while self.current_token.value == '@' :
            self.read()
            if self.current_token.type == '<IDENTIFIER>':
                self.read()
                self.R()
            #print('Ap->R @ <IDENTIFIER> R')
                self.buildTree("@", 2)

    def R(self):
        #print('procR')

        self.Rn()
        #print('R->Rn')

        while (self.current_token.type in ['<IDENTIFIER>', '<INTEGER>',
                                           '<STRING>'] or self.current_token.value in ['true', 'false',
                                                                                                        'nil', 'dummy',
                                                                                                        "("]):
            self.Rn()
            #print('R->R Rn')
            self.buildTree("gamma", 2)


    def Rn(self):

        if self.current_token.type in ['<IDENTIFIER>', '<INTEGER>',
                                           '<STRING>']:

            #print('Rn->' + str(self.current_token.value))
            self.buildTree(self.current_token.value, 0)
            self.read()

        elif self.current_token.value in ['true', 'false', 'nil', 'dummy']:
            #print('Rn->' + self.current_token.value)
            self.buildTree(self.current_token.value, 0)
            self.read()

        elif self.current_token.value == '(':
            self.read()
            self.E()
            if self.current_token.value != ')':
                raise CustomError("Error: ) is expected")

            self.read()
            #print('Rn->( E )')

    def D(self):

        self.Da()
        #print('D->Da')
        while self.current_token.value == 'within':
            self.read()
            self.D()
            #print('D->Da within D')
            self.buildTree("within", 2)

    def Da(self):

        self.Dr()
        #print('Da->Dr')
        n = 1
        while self.current_token.value == 'and':
            n += 1
            self.read()
            self.Da()
            #print('Da->and Dr')
        self.buildTree("and", n)

    def Dr(self):

        if self.current_token.value == 'rec':
            self.read()
            self.Db()
            #print('Dr->rec Db')
            self.buildTree("rec", 1)

        self.Db()
        #print('Dr->Db')

    def Db(self):

        if self.current_token.value == '(':
            self.read()
            self.D()
            if self.current_token.value != ')':
                raise CustomError("Error: ) is expected")

            self.read()
            #print('Db->( D )')

        elif self.current_token.type == '<IDENTIFIER>':
            self.read()

            if self.current_token.type == ',':
                # Db -> Vl '=' E => '='
                self.read()
                self.Vb()

                if self.current_token.value != '=':
                    raise CustomError("Error: = is expected")

                self.buildTree(",", 2)
                self.read()
                self.E()
                self.buildTree("=", 2)

            else :
                if self.current_token.value == '=':
                    self.read()
                    self.E()
                    #print('Db->id = E')
                    self.buildTree("=", 2)

                else :

                    n = 0
                    while self.current_token.type == '<IDENTIFIER>' or self.current_token.value == '(':
                        self.Vb()
                        n += 1

                    if n == 0:
                        raise CustomError("Error: ID or ( is expected")

                    if self.current_token.value != '=':
                        raise CustomError("Error: = is expected")

                    self.read()
                    self.E()
                    #print('Db->identifier Vb+ = E')
                    self.buildTree("function_form", n + 2)

    def Vb(self):
        if self.current_token.type == '<IDENTIFIER>':
            self.buildTree(self.current_token.value , 0)
            self.read()
            #print('Vb->id')

        elif self.current_token.value == '(':
            self.read()

            if self.current_token.type == ')':
                #print('Vb->( )')
                self.buildTree("()", 0)
                self.read()
            else:
                self.VL()
                #print('Vb->( Vl )')
                if self.current_token.value != ')':
                    raise CustomError(') is expected')
            self.read()

        else:
            raise CustomError("Error: ID or ( is expected")

    def VL(self):

        if self.current_token.type != '<IDENTIFIER>': raise CustomError('<IDENTIFIER> is expected')

        else:
            self.buildTree(self.current_token.value , 0)
            self.read()
            n = 1
            while self.current_token.value == ',':
                self.read()
                if self.current_token.type != '<IDENTIFIER>': raise CustomError('<IDENTIFIER> is expected')
                self.buildTree('?',0)
                self.read()
                #print('VL->id , ?')

                n += 1

            if n > 1:
                self.buildTree(',', n)  # +1 for the first identifier





'''
import sys
input_path= "E:\VSCODE\RPAL\src\Tests\\2-t1.txt"
with open(input_path) as file:
    program = file.read();'''

stack = []
tokens = []

for token in Scanner.Tokens:
    if token.value in RESERVED_KEYWORDS:
        token.type = token.value

    tokens.append(token)

parser = ASTParser()
parser.tokens = tokens
parser.current_token = tokens[0]
parser.index = 0

parser.E()
root = stack[0]
root.print_tree()
with open("E:\VSCODE\RPAL\src\OutputParser.txt", "w") as file:
    root.indentation = 0
    root.print_tree_to_file(file)