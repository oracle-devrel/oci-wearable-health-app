import * as React from "react";
// import Link from "@mui/material/Link";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Typography from "@mui/material/Typography";
import { Device } from "./../../../apis";
import dayjs from "dayjs";
import utc from "dayjs/plugin/utc";
import { Stack } from "@mui/system";
import Chip, { ChipTypeMap } from "@mui/material/Chip";
dayjs.extend(utc);

const getChipColor = (status: Device["status"]) => {
  let color: ChipTypeMap["props"]["color"];
  switch (status?.toUpperCase()) {
    case "NEW": {
      color = "secondary";
      break;
    }
    case "LINKED": {
      color = "success";
      break;
    }
    case "IN-ACTIVE": {
      color = "default";
      break;
    }
    case "ACTIVE": {
      color = "primary";
      break;
    }
    default: {
      color = "primary";
    }
  }
  return color;
};

const DeviceListTable: React.FC<{
  devices: Device[] | undefined;
}> = ({ devices }) => {
  return (
    <>
      <Typography component="h3" variant="h6" color="primary" gutterBottom>
        {"Device list"}
      </Typography>

      <Table size="small">
        <TableHead>
          <TableRow>
            <TableCell>
              <Typography>Device serial number</Typography>
            </TableCell>
            <TableCell>
              <Typography> Device status</Typography>
            </TableCell>
            <TableCell>
              <Typography>Device activation date</Typography>
            </TableCell>
            <TableCell>
              <Typography>Device type</Typography>
            </TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {!devices || devices?.length === 0 ? (
            <TableRow>
              <TableCell colSpan={4}>
                <Typography align="center">
                  No device has been added to this account, please add a device
                  first.
                </Typography>
              </TableCell>
            </TableRow>
          ) : (
            devices.map((device, index) => (
              <TableRow key={`${device?.deviceSerialNumber}-${index}`}>
                <TableCell>{device.deviceSerialNumber}</TableCell>
                <TableCell>
                  <Stack direction="row" alignItems="center">
                    <Chip
                      label={device.status?.toUpperCase()}
                      color={getChipColor(device.status)}
                    />
                  </Stack>
                </TableCell>
                <TableCell>
                  {dayjs(device.activationDate as string).isValid()
                    ? dayjs(device.activationDate as string)
                        .utc()
                        .format("YYYY-MM-DD")
                        .toString()
                    : null}
                </TableCell>
                <TableCell>{device.deviceType}</TableCell>
              </TableRow>
            ))
          )}
        </TableBody>
      </Table>
    </>
  );
};
export default DeviceListTable;
