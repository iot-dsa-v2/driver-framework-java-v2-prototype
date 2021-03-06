#from output_parser import *

addprefix = "Invoking /main/"
addsuffix = "Add"
goodstatus = "Status:Connected"
name_string = "\"Name\":\""
tr = Tracker()

steps = parse("testing-output.txt")
for i in range(0, len(steps)):
    step = steps[i]
    if step.action.startswith(addprefix):
        path = step.action[len(addprefix):].strip().split("/")
        if path[-1].startswith(addsuffix):
            #Cleanup name of last node
            new_name = path.pop().split(name_string)[1]
            new_name = new_name.split("\"")[0]
            path.append(new_name)
            dsapoint = find_in_dsa_tree(step.dsa_tree, path)
            tr.main_test(dsapoint is not None, i)

tr.report()