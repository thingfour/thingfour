import {
    DataTable,
} from "@carbon/react";
import dayjs from "dayjs";
import {BasePage} from "@thing4/core-ui";

const {
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableHeader,
    TableRow,
} = DataTable;

interface PersistenceManagerProps {}

interface DataEntry {
    time: number;
    value: number;
}

export function PersistenceManager({} : PersistenceManagerProps) {

    const headers = ['#', 'Time', 'Value']
    const data : DataEntry[] = [
        {time: new Date().getMilliseconds() - 1000, value: 10.0},
        {time: new Date().getMilliseconds(), value: 10.0}
    ]

    return (
        <BasePage>
            <TableContainer title="Audit log" description="Messages received by all servers" style={{width: "100%", height: "100%"}}>
                <Table size="xs" useZebraStyles={false} aria-label="sample table">
                    <TableHead>
                        <TableRow>
                            {headers.map((header, id) => <TableHeader key={id}>{header}</TableHeader>)}
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {data.map((row, idx) => {
                            return (
                                <>
                                    <TableRow key={idx + 1}>
                                        <TableCell>{idx + 1}</TableCell>
                                        <TableCell>{dayjs.unix(row.time / 1000).format('DD.MM.YYYY HH:mm:ss')}</TableCell>
                                        <TableCell>${row.value}</TableCell>
                                    </TableRow>
                                </>
                            )
                        })}
                    </TableBody>
                </Table>
            </TableContainer>
        </BasePage>
    )
}