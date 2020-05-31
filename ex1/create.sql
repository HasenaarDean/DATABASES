CREATE TABLE Pilot
(
	pilotId INTEGER PRIMARY KEY, 
	name VARCHAR(50) NOT NULL
);

CREATE TABLE Airplane
(
	airplaneId INTEGER PRIMARY KEY,
	model VARCHAR(50) NOT NULL,
	productionYear INTEGER NOT NULL
);

CREATE TABLE Flight
(
	flightId INTEGER NOT NULL,
	flightDate DATE NOT NULL,
	hour TIME NOT NULL,
	airplaneId INTEGER NOT NULL,
	PRIMARY KEY (flightId),
	FOREIGN KEY (airplaneId) REFERENCES Airplane(airplaneId) ON DELETE CASCADE
	
);

CREATE TABLE PilotOfFlight
(
	flightId INTEGER NOT NULL,
	pilotId INTEGER NOT NULL,
	PRIMARY KEY (flightId, pilotId), 
	FOREIGN KEY (flightId) REFERENCES Flight(flightId) ON DELETE CASCADE,
	FOREIGN KEY (pilotId) REFERENCES Pilot(pilotId) ON DELETE CASCADE
	

);

CREATE TABLE FlightChair
(
	chairRowNumber INTEGER CHECK (chairRowNumber >= 1 AND chairRowNumber <=
	                                                      20) NOT NULL,
	chairLetter CHAR(1) CHECK ( chairLetter >= 'A' AND chairLetter <= 'J') NOT
	    NULL,
	flightId INTEGER NOT NULL,
	PRIMARY KEY (chairRowNumber, chairLetter, flightId),
	FOREIGN KEY (flightId) REFERENCES Flight(flightId) ON DELETE CASCADE
	
);

CREATE TABLE SimpleFlightChair
(
	chairRowNumber INTEGER CHECK (chairRowNumber >= 1 AND chairRowNumber <=
	                                                      20) NOT NULL,
	chairLetter CHAR(1) CHECK ( chairLetter >= 'A' AND chairLetter <= 'J')
	    NOT NULL,
	flightId INTEGER NOT NULL,
	PRIMARY KEY (chairRowNumber, chairLetter, flightId),
	FOREIGN KEY (chairRowNumber, chairLetter, flightId) REFERENCES FlightChair
	    (chairRowNumber, chairLetter, flightId) ON DELETE CASCADE

);

CREATE TABLE VipFlightChair
(
	chairRowNumber INTEGER CHECK (chairRowNumber >= 1 AND chairRowNumber <=
	                                                      20) NOT NULL,
	chairLetter CHAR(1) CHECK ( chairLetter >= 'A' AND chairLetter <= 'J')
	    NOT NULL,
	flightId INTEGER NOT NULL,
	PRIMARY KEY (chairRowNumber, chairLetter, flightId),
	FOREIGN KEY (chairRowNumber, chairLetter, flightId) REFERENCES FlightChair
	    (chairRowNumber, chairLetter, flightId) ON DELETE CASCADE

);

CREATE TABLE Client
(
	clientId INTEGER NOT NULL, 
	clientName VARCHAR(50) NOT NULL,
	clientPhoneNumber INTEGER NOT NULL, 
	clientDateOfBirth DATE,
	PRIMARY KEY (clientId)
	
);

CREATE TABLE VipClient
(
	
	clientId INTEGER NOT NULL,
	clientSumOfPoints INTEGER CHECK ( clientSumOfPoints > 0 ) NOT NULL,
	PRIMARY KEY (clientId),
	FOREIGN KEY (clientId) REFERENCES Client(clientId) ON DELETE CASCADE

);

CREATE TABLE SimpleChairOrder
(

	clientId INTEGER NOT NULL,
	chairRowNumber INTEGER CHECK (chairRowNumber >= 1 AND chairRowNumber <=
	                                                      20) NOT NULL,
	chairLetter CHAR(1) NOT NULL, 
	flightId INTEGER NOT NULL,
	orderPrice INTEGER CHECK ( orderPrice > 0 ) NOT NULL,
	PRIMARY KEY (clientId, chairRowNumber, chairLetter, flightId),
	FOREIGN KEY (clientId) REFERENCES Client(clientId) ON DELETE CASCADE,
	FOREIGN KEY (chairRowNumber, chairLetter, flightId) REFERENCES
	    SimpleFlightChair(chairRowNumber, chairLetter, flightId) ON DELETE
	        CASCADE

);

CREATE TABLE VipChairOrder
(

	clientId INTEGER NOT NULL,
	chairRowNumber INTEGER CHECK (chairRowNumber >= 1 AND chairRowNumber <=
	                                                      20) NOT NULL,
	chairLetter CHAR(1) NOT NULL, 
	flightId INTEGER NOT NULL,
	orderPrice INTEGER CHECK ( orderPrice > 0 ) NOT NULL,
	PRIMARY KEY (clientId, chairRowNumber, chairLetter, flightId),
	FOREIGN KEY (clientId) REFERENCES VipClient(clientId) ON DELETE CASCADE,
	FOREIGN KEY (chairRowNumber, chairLetter, flightId) REFERENCES
	    FlightChair(chairRowNumber, chairLetter, flightId) ON DELETE CASCADE

);
