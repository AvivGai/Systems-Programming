import sys
import sqlite3
import os


def print_tables(cursor):
    print("courses")
    courses = cursor.execute("SELECT * FROM courses").fetchall()
    for line in courses:
        print(line)
    print("classrooms")
    classrooms = cursor.execute("SELECT * FROM classrooms").fetchall()
    for line in classrooms:
        print(line)
    print("students")
    students = cursor.execute("SELECT * FROM students").fetchall()
    for line in students:
        print(line)


def main(args):
    database_existed = os.path.isfile('schedule.db')
    db_con = sqlite3.connect('schedule.db')
    db_con.text_factory = str
    with db_con:
        cursor = db_con.cursor()
        if not database_existed:  # First time creating the database. Create the tables
            cursor.execute("CREATE TABLE students(grade TEXT PRIMARY KEY, count INTEGER NOT NULL)")
            cursor.execute("CREATE TABLE courses(id INTEGER PRIMARY KEY, course_name TEXT NOT NULL, student TEXT NOT "
                           "NULL,number_of_students INTEGER NOT NULL, class_id INTEGER REFERENCES classrooms(id), "
                           "course_length INTEGER NOT NULL)")
            cursor.execute("CREATE TABLE classrooms(id INTEGER PRIMARY KEY, location TEXT NOT NULL, current_course_id "
                           "INTEGER NOT NULL, current_course_time_left INTEGER NOT NULL)")
        input_file_name = args[1]
        with open(input_file_name) as input_file:
            for line in input_file:
                line_as_list = line.split(',')
                if line_as_list[0].strip() == 'S':
                    cursor.execute("INSERT INTO students VALUES(?,?)", (line_as_list[1].strip(), line_as_list[2].strip()))
                elif line_as_list[0].strip() == 'C':
                    cursor.execute("INSERT INTO courses VALUES(?,?,?,?,?,?)",
                                   (line_as_list[1].strip(), line_as_list[2].strip(), line_as_list[3].strip(), line_as_list[4].strip(), line_as_list[5].strip(),
                                    line_as_list[6].strip()))
                else:
                    cursor.execute("INSERT INTO classrooms VALUES(?,?,?,?)",
                                   (line_as_list[1].strip(), line_as_list[2].strip(), 0, 0))
        print_tables(cursor)


if __name__ == '__main__':
    main(sys.argv)
