import Scanner

#from Scanner import Token

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
        print(self.type)

        if self.child:
            #print(" child of " + str(self.type) + " is ",end=" ")
            self.child.print_tree()

        if self.sibling:
            #print(" sibling of " + str(self.type) + " is " ,end=" ")

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


# class ASTNode:
#     def __init__(self, value, children):
#         self.value = value
#         self.children = children
#
#     def __str__(self):
#         return f"ASTNode({self.value}, {self.children})"
#
#     def __repr__(self):
#         return f"ASTNode({self.value}, {self.children})"


class TreeNode:
    def __init__(self, data):
        self.data = data
        self.children = []
        self.parent = None

    def add_child(self, child):
        child.parent = self
        self.children.append(child)

    # def print_tree(self):
    #
    #     print("parent node : " + self.data)
    #     if self.children:
    #         count = 0
    #         for child in self.children:
    #             print("child node : " + child.data)
    #         for child in self.children:
    #             child.print_tree()

    # pre-order traversal of n araay tree
    def print_tree(self):
        print(self.data)
        if self.children:
            for child in self.children:
                child.print_tree()


class ASTParsser:

    def __int__(self, tokens1):
        self.tokens = tokens1
        self.current_token = None
        self.index = 0

    def read(self):

        if self.current_token.type == ('<IDENTIFIER>' or '<INTEGER>' or '<STRING>') :

            terminalNode = ASTNode( "<"+str(self.current_token.type)+":"+  str(self.current_token.value)+">")
            stack.append(terminalNode)
            # print stack
            # print("stack content after reading")
            # for node in stack:
            #     print(node.data)
        if self.current_token.value in  ['true', 'false', 'nil', 'dummy']:
            stack.append(ASTNode(self.current_token.value))

        #print("reading : " + str(self.current_token.value))
        self.index += 1

        if (self.index < len(self.tokens)):
            self.current_token = self.tokens[self.index]

    def buildTree(self, token, ariness):
        global stack

        #print("stack content before ")
        for node in stack:
            print(node.type)

        #print("building tree")

        node = ASTNode(token)
        node.value = None
        node.sourceLineNumber = -1
        node.child = None
        node.sibling = None

        while ariness > 0:
            # print("error in while loop")
            child = stack[-1]
            stack.pop()
            # Assuming pop() is a function that returns an ASTNode
            if node.child is not None:
                child.sibling = node.child
            node.child = child
            node.sourceLineNumber = child.sourceLineNumber
            ariness -= 1
            # print("test")
        # print("root", node.type)
        node.print_tree()

        stack.append(node)  # Assuming push() is a function that pushes a node onto a stack
        #print("stack content after")
        for node in stack:
            print(node.type)
    #
    #     # return node

    # def buildTree(self,token1, n):
    #     node = TreeNode(token1)
    #     # print(token)
    #     global stack
    #     children = []
    #     # print(stack[-1].data)
    #
    #     # stack[-1].print_tree()
    #     for i in range(n):
    #         children.append(stack[-1])
    #         stack.pop()
    #
    #     node.children = children
    #     stack.append(node)
    #     print("after buuilding parent :" + stack[-1].data)
    #     # print(stack[-1].data)
    #     children = stack[-1].children
    #     ## print children
    #     for child in children:
    #         print(child.data)
    #
    #     print("printing stack")
    #     for child in stack:
    #         print(child.data)
    #     print("############")

    def procE(self):
        #print('procE')

        if self.current_token.value == 'let':
            self.read()
            self.procD()

            if self.current_token.value != 'in':
                #print("Error: in is expected")
                return

            self.read()
            self.procE()
            #print('E->let D in E')
            self.buildTree("let", 2)

        elif self.current_token.value == 'fn':

            n = 0

            self.read()

            while self.current_token.type == '<IDENTIFIER>' or self.current_token.value == '(':
                self.procVb()
                n += 1

            if n == 0:
                #print("E: at least one 'Vb' expected\n")
                return

            if self.current_token.value != '.':
                #print("Error: . is expected")
                return

            self.read()
            self.procE()
            #print('E->fn Vb . E')
            self.buildTree("lambda", n+1)

        else:
            self.procEw()
            #print('E->Ew')

    def procEw(self):
        #print('procEw')
        self.procT()
        #print('Ew->T')
        if self.current_token.value == 'where':
            self.read()
            self.procDr()
            #print('Ew->T where Dr')
            self.buildTree("where", 2)

    def procT(self):
        #print('procT')
        self.procTa()
        # print('T->Ta')

        n = 0
        while self.current_token.value == ',':
            self.read()
            self.procTa()
            n += 1
            #print('T->Ta , Ta')
        if n > 0:
            self.buildTree("tau", n + 1)

    def procTa(self):
        #print('procTa')
        self.procTc()
        #print('Ta->Tc')
        while self.current_token.value == 'aug':
            self.read()
            self.procTc()
            #print('Ta->Tc aug Tc')

            self.buildTree("aug", 2)

    def procTc(self):
        #print('procTc')

        self.procB()
        #print('Tc->B')
        if self.current_token.type == '->':
            self.read()
            self.procTc()

            if self.current_token.value != '|':
                #print("Error: | is expected")
                return
            self.read()
            self.procTc()
            #print('Tc->B -> Tc | Tc')
            self.buildTree("->", 3)

    def procB(self):
        #print('procB')

        self.procBt()
        #print('B->Bt')
        while self.current_token.value == 'or':
            self.read()
            self.procBt()
            #print('B->Bt or Bt')
            self.buildTree("or", 2)

    def procBt(self):
        #print('procBt')

        self.procBs()
        #print('Bt->Bs')
        while self.current_token.value == '&':
            self.read()
            self.procBs()
            #print('Bt->Bs & Bs')
            self.buildTree("&", 2)

    def procBs(self):
        #print('procBs')

        if self.current_token.value == 'not':
            self.read()
            self.procBp()
            #print('Bs->not Bp')
            self.buildTree("not", 1)
        else:
            self.procBp()
            #print('Bs->Bp')

    def procBp(self):
        #print('procBp')

        self.procA()
        #print('Bp->A')
        #print(self.current_token.value+"######")

        ##  Bp -> A ( 'gr' | '>') A
        if self.current_token.value == '>'or self.current_token.value == 'gr' :
            self.read()
            self.procA()
            #print('Bp->A (gr|>) A')
            self.buildTree("gr", 2)


        #elif self.current_token.value == 'gr':
            #self.read()
            #self.procA()
            #print('Bp->A gr A')
            #self.buildTree("gr", 2)

        elif self.current_token.value == 'ge' or self.current_token.value == '>=':
            self.read()
            self.procA()
            #print('Bp->A (ge|>=) A')
            self.buildTree("ge", 2)

        #elif self.current_token.value == '>=':
            #self.read()
            #self.procA()
            #print('Bp->A ge A')
            #self.buildTree("ge", 2)



        elif self.current_token.value == '<' or  self.current_token.value == 'ls':
            self.read()
            self.procA()
            #print('Bp->A (ls| <) A')
            self.buildTree("ls", 2)

        #elif self.current_token.value == 'ls':
            #self.read()
            #self.procA()
            #print('Bp->A ls A')
            #self.buildTree("ls", 2)

        elif self.current_token.value == '<=' or self.current_token.value == 'le':
            self.read()
            self.procA()
            #print('Bp->A (le| <=) A')
            self.buildTree("le", 2)

        #elif self.current_token.value == 'le':
            #self.read()
            #self.procA()
            #print('Bp->A le A')
            #self.buildTree("le", 2)

        elif self.current_token.value == 'eq':
            self.read()
            self.procA()
            #print('Bp->A eq A')
            self.buildTree("eq", 2)

        elif self.current_token.value == 'ne':
            self.read()
            self.procA()
            #print('Bp->A ne A')
            self.buildTree("ne", 2)

        else:
            return

    def procA(self):
        #print('procA')

        if self.current_token.value == '+':
            self.read()
            self.procAt()
            #print('A->+ At')
            # self.buildTree("+", 1)

        elif self.current_token.value == '-':
            self.read()
            self.procAt()
            #print('A->- At')
            self.buildTree("neg", 1)


        else:
            self.procAt()
            #print('A->At')
        plus = '+'
        while self.current_token.value == '+' or self.current_token.value == '-':

            if self.current_token.value=='-':
                plus='-'

            self.read()
            self.procAt()
            #print('A->At + / - At')
            #print(self.current_token.value)
            self.buildTree(plus, 2)


    def procAt(self):
        #print('procAt')

        self.procAf()
        #print('At->Af')
        while self.current_token.value == '*' or self.current_token.value == '/':
            self.read()
            self.procAf()
            #print('At->Af *|/ Af')
            #print("current token value " + self.current_token.value)
            self.buildTree(self.current_token.value, 2)

    def procAf(self):
        #print('procAf')

        self.procAp()
        #print('Af->Ap')
        if self.current_token.value == '**':
            self.read()
            self.procAf()
            #print('Af->Ap ** Af')
            self.buildTree("**", 2)

    def procAp(self):
        #print('procAp')

        self.procR()
        #print('Ap->R')
        while self.current_token.value == '@' :
            self.read()
            self.procR()
            #print('Ap->R @ <IDENTIFIER> R')
            self.buildTree("@", 2)

    def procR(self):
        #print('procR')

        self.procRn()
        #print('R->Rn')
        # self.read()

        while (self.current_token.type in ['<IDENTIFIER>', '<INTEGER>',
                                           '<STRING>'] or self.current_token.value in ['true', 'false',
                                                                                                        'nil', 'dummy',
                                                                                                        "("]):
            self.procRn()
            #print('R->R Rn')
            self.buildTree("gamma", 2)

            # self.read()

    def procRn(self):
        #print("procRn")

        if self.current_token.type in ['<IDENTIFIER>', '<INTEGER>',
                                           '<STRING>']:

            #print('Rn->' + str(self.current_token.value))

            self.read()

            # self.read()
            #self.buildTree (str(self.current_token.value), 0)

        elif self.current_token.value in ['true', 'false', 'nil', 'dummy']:
            #print('Rn->' + self.current_token.value)
            self.read()
            #print("self.current_token.value" , self.current_token.value)
            self.buildTree(self.current_token.value, 0)

        elif self.current_token.value == '(':
            self.read()
            self.procE()
            if self.current_token.value != ')':
                #print("Error: ) is expected")
                return
            self.read()
            #print('Rn->( E )')
            #self.buildTree("()", 1)

    def procD(self):
        #print('procD')

        self.procDa()
        #print('D->Da')
        while self.current_token.value == 'within':
            self.read()
            self.procD()
            #print('D->Da within D')
            self.buildTree("within", 2)

    def procDa(self):
        #print('procDa')

        self.procDr()
        #print('Da->Dr')
        n = 0
        while self.current_token.value == 'and':
            n += 1
            self.read()
            self.procDa()
            #print('Da->and Dr')
        # if n == 0:
        #     print("Error")
        #     return
        if n > 0:
            self.buildTree("and", n + 1)

    def procDr(self):
        #print('procDr')

        if self.current_token.value == 'rec':
            self.read()
            self.procDb()
            #print('Dr->rec Db')
            self.buildTree("rec", 1)

        self.procDb()
        #print('Dr->Db')

    def procDb(self):
        #print('procDb')

        if self.current_token.value == '(':
            self.read()
            self.procD()
            if self.current_token.value != ')':
                #print("Error: ) is expected")
                return

            self.read()
            #print('Db->( D )')
            #self.buildTree("()", 1)

        elif self.current_token.type == '<IDENTIFIER>':
            self.read()

            if self.current_token.type == ',':
                # Db -> Vl '=' E => '='
                self.read()
                self.procVb()

                if self.current_token.value != '=':
                    #print("Error: = is expected")
                    return
                self.buildTree(",", 2)
                self.read()
                self.procE()
                self.buildTree("=", 2)
            else :
                if self.current_token.value == '=':
                    self.read()
                    self.procE()
                    #print('Db->id = E')
                    self.buildTree("=", 2)

                else :

                    n = 0
                    while self.current_token.type == '<IDENTIFIER>' or self.current_token.value == '(':
                        self.procVb()
                        n += 1

                    if n == 0:
                        print("Error: ID or ( is expected")
                        return

                    if self.current_token.value != '=':
                        print("Error: = is expected")
                        return
                    self.read()
                    self.procE()
                    #print('Db->identifier Vb+ = E')
                    self.buildTree("function_form", n + 2)

        # else:
        #     self.procVL()
        #     print(self.current_token.value)
        #     if self.current_token.value != '=':
        #         print("Error: = is expected")
        #         return
        #     self.read()
        #     self.procE()
        #     print('Db->Vl = E')
        #     self.buildTree("=", 2)

    def procVb(self):
        #print('procVb')
        if self.current_token.type == '<IDENTIFIER>':
            self.read()
            #print('Vb->id')
            # self.buildTree("id", 1)

        elif self.current_token.value == '(':
            self.read()
            # print(self.current_token.value)
            if self.current_token.type == ')':
                #print('Vb->( )')
                self.buildTree("()", 0)
                self.read()
            else:
                self.procVL()
                #print('Vb->( Vl )')
                if self.current_token.value != ')':
                    print("Error: ) is expected")
                    return
            self.read()

            # self.buildTree("()", 1)

        else:
            print("Error: ID or ( is expected")
            return

    def procVL(self):
        #print("procVL")
        # str(self.current_token.value)print("559 "+str(self.current_token.value))

        if self.current_token.type != '<IDENTIFIER>': return
            #print("562 VL: Identifier expected")  # Replace with appropriate error handling
        else:
            #print('VL->' + self.current_token.value)

            self.read()
            trees_to_pop = 0
            while self.current_token.value == ',':
                # Vl -> '<IDENTIFIER>' list ',' => ','?;
                self.read()
                if self.current_token.type != '<IDENTIFIER>': return
                    #print(" 572 VL: Identifier expected")  # Replace with appropriate error handling
                self.read()
                #print('VL->id , ?')
                self.buildTree('?',0)

                trees_to_pop += 1
            #print('498')
            if trees_to_pop > 0:
                self.buildTree(',', trees_to_pop +1)  # +1 for the first identifier





import sys
input_path= "E:\VSCODE\RPAL\src\Tests\\1-t1.txt"
with open(input_path) as file:
    program = file.read();
    # print(program)

stack = []
tokens = []
# tokenize input
#tokenizer = Scanner.Token(program)
#token = tokenizer.get_next_token()
#print(token.type, token.value)
#while token.type != Scanner.:
    #print(token.type, token.value)
for token in Scanner.Tokens:
    if Scanner.token.value in RESERVED_KEYWORDS:
        Scanner.token.type = Scanner.token.value

    tokens.append(token)

#screener = Scanner(tokens)
#tokens = screener.screen()
# parse input
#print(" after screening ")
# for token in tokens:
#     # print(token.type, token.value)
parser = ASTParsser()
parser.tokens = tokens
parser.current_token = tokens[0]
parser.index = 0

parser.procE()
#print(len(stack))
root = stack[0]
root.print_tree()
with open("E:\VSCODE\RPAL\src\OutputParser.txt", "w") as file:
    root.indentation = 0
    root.print_tree_to_file(file)