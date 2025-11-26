# CMakeList.txt : CMake project for Project_2048, include source and define
# project specific logic here.
#

# Add source to this project's executable.
add_executable (Project_2048 "Project_2048.cpp" "Project_2048.h" "functions.h"  )

if (CMAKE_VERSION VERSION_GREATER 3.12)
  set_property(TARGET Project_2048 PROPERTY CXX_STANDARD 20)
endif()

# TODO: Add tests and install targets if needed.
