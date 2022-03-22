#!python
# Written by Bradley McFadden on 3/22/2022
# Takes .obj files and collapses duplicate material groups
# into single groups. So, many instances of usemtl Mat1
# would be collapsed into a single group with all the model
# faces that use Mat1 in series
# usage: python model_converter.py

def main():
    filename = input("Please enter the filename: ")
    material_groups = {}
    header_group = []
    last_mg = ''
    print("Reading file...")
    with open(filename, 'r') as f:
        line = f.readline()
        while (line):
            if line == "\n":
                header_group.append(line)
                continue
            tokens = line.split(" ")

            if len(tokens) == 0:
                header_group.append(line)
                continue

            line_start = tokens[0]

            if line_start == "usemtl":
                mg_name = tokens[1].strip()
                if not mg_name in material_groups:
                    print(f"Found new material group: {mg_name}")
                    material_groups[mg_name] = []
                last_mg = mg_name
            elif line_start == "f":
                material_groups[last_mg].append(line)
            elif not line_start in ["l", "s", "g"]:
                header_group.append(line)

            line = f.readline()
    # endwhile

    print("Writing to new file...")
    with open("new_" + filename, 'w') as f:
        for line in header_group:
            f.write(line)

        for k, v in material_groups.items():
            f.write(f"usemtl {k}\n")

            for line in v:
                f.write(line)
        # endfor
    # endwith
    print("Done.")
    print(f"Modified object written to new_{filename}.")

if __name__ == "__main__":
    main()
