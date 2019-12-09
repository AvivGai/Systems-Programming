import os
import sqlite3


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


def main():
    database_existed = os.path.isfile('schedule.db')
    if database_existed:
        db_con = sqlite3.connect('schedule.db')
        db_con.text_factory = str
        with db_con:
            cursor = db_con.cursor()
            counter = 0
            course_info = cursor.execute("SELECT * FROM courses").fetchall()
            if len(course_info) == 0:
                print_tables(cursor)
            while len(course_info) > 0:
                iteration_rooms = []
                for line in course_info:
                    course_id = line[0]
                    course_name = line[1]
                    course_student = line[2]
                    course_num_students = line[3]
                    classroom_id = line[4]
                    course_length = line[5]
                    classroom_info = cursor.execute("SELECT * FROM classrooms WHERE id=(?)", (classroom_id,)).fetchone()
                    classroom_location = classroom_info[1]
                    classroom_course_id = classroom_info[2]
                    classroom_time_left = classroom_info[3]
                    student_info = cursor.execute("SELECT * FROM students WHERE grade=(?)", (course_student,)).fetchone()
                    student_grade = student_info[0]
                    student_count = student_info[1]
                    if classroom_id not in iteration_rooms:
                        if classroom_course_id == 0 and classroom_time_left == 0:
                            iteration_rooms.append(classroom_id)
                            print("("+str(counter)+") "+str(classroom_info[1])+": "+str(course_name)+" is schedule to start")
                            cursor.execute("UPDATE classrooms SET current_course_id=(?) WHERE id=(?)", (course_id, classroom_id))
                            cursor.execute("UPDATE classrooms SET current_course_time_left=(?) WHERE id=(?)", (course_length, classroom_id))
                            cursor.execute("UPDATE students SET count=(?) WHERE grade=(?)", (student_count-course_num_students, course_student))
                        elif classroom_time_left > 1:
                            iteration_rooms.append(classroom_id)
                            course_name = cursor.execute("SELECT course_name FROM courses WHERE id=(?)",
                                                     (classroom_course_id,)).fetchone()
                            print("(" + str(counter) + ") " + str(classroom_location) + ": occupied by " + str(
                            course_name[0]))
                            cursor.execute("UPDATE classrooms SET current_course_time_left=(?) WHERE id=(?)",
                                       (classroom_time_left - 1, classroom_id))
                        else:
                            print("(" +str(counter) + ") " + str(classroom_location) + ": " + str(course_name) + " is done")
                            cursor.execute("DELETE FROM courses WHERE id=(?)", (classroom_course_id,))
                            if course_id != classroom_course_id:
                                print("(" + str(counter) + ") " + str(course_name) + ": " + str(
                                course_name) + " is schedule to start")
                                cursor.execute("UPDATE classrooms SET current_course_id=(?) WHERE id=(?)",
                                           (course_id, classroom_id))
                                cursor.execute("UPDATE classrooms SET current_course_time_left=(?) WHERE id=(?)",
                                           (course_length, classroom_id))
                                cursor.execute("UPDATE students SET count=(?) WHERE grade=(?)",
                                           (student_count - course_num_students, course_student))
                            else:
                                cursor.execute("UPDATE classrooms SET current_course_id=(?) WHERE id=(?)",
                                           (0, classroom_id))
                                cursor.execute("UPDATE classrooms SET current_course_time_left=(?) WHERE id=(?)",
                                           (0, classroom_id))
                counter = counter+1
                course_info = cursor.execute("SELECT * FROM courses").fetchall()
                print_tables(cursor)


if __name__ == '__main__':
    main()
