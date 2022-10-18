import * as React from "react";
import { useTheme } from "@mui/material/styles";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import Button from "@mui/material/Button";
import KeyboardArrowLeft from "@mui/icons-material/KeyboardArrowLeft";
import KeyboardArrowRight from "@mui/icons-material/KeyboardArrowRight";
import SwipeableViews from "react-swipeable-views";
import { autoPlay } from "react-swipeable-views-utils";

const AutoPlaySwipeableViews = autoPlay(SwipeableViews);

const images = [
  {
    title: "Fullfill your dreams...",
    content: "Instant loan application offers fast processing of your loan.",
    imgPath: "/assets/carousel-1.jpeg",
  },
  {
    title: "Powered with Cloud Automation ",
    content:
      "Complete automated workflow system that supports auto loan approvals and faster loan processings.",
    imgPath: "/assets/carousel-2.jpeg",
  },
];

function SwipeableTextMobileStepper() {
  const theme = useTheme();
  const [activeStep, setActiveStep] = React.useState(0);
  const maxSteps = images.length;

  const handleNext = () => {
    setActiveStep((prevActiveStep) => prevActiveStep + 1);
  };

  const handleBack = () => {
    setActiveStep((prevActiveStep) => prevActiveStep - 1);
  };

  const handleStepChange = (step: number) => {
    setActiveStep(step);
  };
  return (
    <>
      <AutoPlaySwipeableViews
        style={{
          width: "100vw",
          height: "100vh",
          position: "absolute",
          top: 0,
          left: 0,
          zIndex: 0,
        }}
        axis={theme.direction === "rtl" ? "x-reverse" : "x"}
        index={activeStep}
        onChangeIndex={handleStepChange}
        enableMouseEvents
        interval={8000}
        springConfig={{
          duration: "3s",
          easeFunction: "ease-in-out",
          delay: "0s",
        }}
      >
        {images.map((step, index) => {
          return Math.abs(activeStep - index) <= 2 ? (
            <React.Fragment key={step.title}>
              <Box
                component="div"
                sx={{
                  position: "relative",
                  zIndex: "-100",
                  height: "100vh",
                  objectFit: "cover",
                  width: "100vw",
                  top: "0",
                  left: "0",
                }}
              >
                <Box
                  component="img"
                  sx={{
                    objectFit: "cover",
                    position: "relative",
                    zIndex: "-100",
                    height: "100%",
                    width: "100%",
                    top: "0",
                    left: "0",
                  }}
                  src={step.imgPath}
                  alt={step.title}
                />
                <Box
                  style={{
                    position: "absolute",
                    top: "1rem",
                    display: "flex",
                    flexDirection: "column",
                  }}
                >
                  <Typography
                    style={{
                      marginLeft: "1rem",
                      fontSize: "calc(1.525rem + 3.3vw)",
                      fontWeight: "700",
                      lineHeight: "1.2",
                      marginBottom: "2rem",
                    }}
                  >
                    {step.title}
                  </Typography>
                  <Typography
                    style={{
                      marginLeft: "1rem",
                      color: "#696E77",
                      fontSize: "1.25rem",
                      maxWidth: "20vw",
                      fontWeight: "400",
                    }}
                  >
                    {step.content}
                  </Typography>
                </Box>
              </Box>
            </React.Fragment>
          ) : null;
        })}
      </AutoPlaySwipeableViews>

      <Box
        component="div"
        style={{
          position: "absolute",
          width: "100vw",
          left: "0",
          height: "3rem",
          top: "calc(50vh - 1.5rem)",
          display: "flex",
          justifyContent: "space-between",
        }}
      >
        <Button
          size="medium"
          color="primary"
          onClick={handleBack}
          disabled={activeStep === 0}
        >
          {theme.direction === "rtl" ? (
            <KeyboardArrowRight fontSize="large" />
          ) : (
            <KeyboardArrowLeft fontSize="large" />
          )}
        </Button>
        <Button
          size="medium"
          color="primary"
          onClick={handleNext}
          disabled={activeStep === maxSteps - 1}
        >
          {theme.direction === "rtl" ? (
            <KeyboardArrowLeft fontSize="large" />
          ) : (
            <KeyboardArrowRight fontSize="large" />
          )}
        </Button>
      </Box>
    </>
  );
}

export default SwipeableTextMobileStepper;
