# EtherNet/IP Client Java Implementation

## Project Overview

This project implements basic ENIP Client used in communication with ENIP server. It was developed as part
of my Bachelor thesis and demonstrates:
-  Explicit messaging
-  Selected CIP services (Get_Attribute_Single, Get_Attribute_All, Get_Attribute_List, Set_Attribute_Single)
-  Forward Open / Forward Close handling
-  Basic implicit mesaging over UDP
-  Modular CIP object library implementation

It provides standalone GUI apllictaion for testing in EtherNet/IP network.

## Architecture

The project is structured into modular components:

-  `Main.java` - core communication logic
-  `ENIP_form.java` - GUI layer
-  `Encapsulation_packet.java` - construcion of basic ENIP packet
-  `Data_segment_parameters.java` - construction of service-specific data segments
-  `ENIP_form.form` - GUI layout definition
-  `CIP_attribute_format.java` - definition of CIP attribute metadata
-  `CIP_segment_parameters.java` - building of CIP path segments
-  `CIP_object_format.java` - CIP object architecture
-  `CIP_object_library.java` - collection of implemented CIP objects
 
 ## Purpose

 The goal of this project was to deeply understand:
 - EtherNet/IP encapsulation
 - CIP object model
 - Explicit vs implicit messaging
 - Industrial communication principles
 
