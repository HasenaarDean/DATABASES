import java.io.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;


public class ExternalMemoryImpl extends IExternalMemory {

	private static final int RAM_SIZE = 30000000;
	private static final int BUFFER_SIZE = 8192;
	private static final int BLOCK_SIZE = 8192;
	private static final int LINE_SIZE = 52;
	private static final int LINES_IN_BLOCK = BLOCK_SIZE / LINE_SIZE;
	private long numCharsInFile = 0;
	private long numOfSequences = 0;
	private long numBlocks = 0; // number of blocks in memory
	private int bufferSize = 0; // bufferSize in blocks
	private ArrayList<BufferedReader> pointers = new ArrayList<>(); // pointers to diffrent position in file
    private ArrayList<ArrayDeque<String>> memory;
    private File tmp;

    private int compareForJoin(String o1, String o2){
		String[] elems1 = o1.split(" ");
		String[] elems2 = o2.split(" ");

		return elems1[0].compareTo(elems2[0]);
	}
    private int compare(String o1, String o2) {
        String[] elems1 = o1.split(" ");
        String[] elems2 = o2.split(" ");
        if(elems1[0].equals(elems2[0])){
            if (elems1[1].equals(elems2[0])){
                return elems1[2].compareTo(elems2[2]);
            }
            return elems1[1].compareTo(elems2[1]);
        }
        return elems1[0].compareTo(elems2[0]);
    }

	private long getBufferSize(long numBlocks){

		return (long) Math.ceil((1.0 + Math.sqrt(1 - 4 * (-1 - numBlocks))) / 2.0);
	}
	private ArrayDeque<String> readBlock(BufferedReader reader) {
        ArrayDeque<String> block = new ArrayDeque<>();
        for(int i = 0; i < LINES_IN_BLOCK; ++i){
            try {
                String line = reader.readLine();
                if(line != null)
                    block.addLast(line);
            } catch (IOException e) {
                System.err.println("error reading line");
            }
        }
	    return block;
    }

	private boolean writeBlock(BufferedWriter writer, Collection<String> block){
		for(String s: block){
            try {
                writer.write(s);
                writer.write('\n');

            } catch (IOException e) {
                return false;
            }

        }
		return true;
	}
	private void phase1Sort(BufferedReader reader, BufferedWriter writerr, int bufferSize, long numBlocks) throws IOException {
		long left = 0;
		long m = 0;
		System.out.println("free memory " + Runtime.getRuntime().freeMemory());
		while(left < numBlocks){
			System.out.println("iteration " + left + " out of " + numBlocks);
			long right = Math.min(left + bufferSize, numBlocks) - 1;
			ArrayList<String> buffer = new ArrayList<>();
			for(long j = left; j <= right; ++j) {
				for (int i = 0; i < LINES_IN_BLOCK; ++i) {
					System.out.println("free memory " + Runtime.getRuntime().freeMemory());
					try {
						String line = reader.readLine();
						if (line != null)
							buffer.add(line);
					} catch (IOException e) {
						System.err.println("error reading line");
					}
				}
			}

			buffer.sort(new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					String[] elems1 = o1.split(" ");
					String[] elems2 = o2.split(" ");
					if(elems1[0].equals(elems2[0])){
						if (elems1[1].equals(elems2[0])){
							return elems1[2].compareTo(elems2[2]);
						}
						return elems1[1].compareTo(elems2[1]);
					}
					return elems1[0].compareTo(elems2[0]);
				}
			});
			File f = File.createTempFile("tmp" + m, ".txt", tmp);
			f.deleteOnExit();
            BufferedWriter writer = new BufferedWriter(new FileWriter(f), BUFFER_SIZE);
			for (int j = 0; j < buffer.size(); ++j) {
				writer.write(buffer.get(j));
				writer.write('\n');

			}
			buffer.clear();
			writer.close();
			BufferedReader r = new BufferedReader(new FileReader(f), BUFFER_SIZE);
			pointers.add(r);
			left += bufferSize;
			++m;

		}
		numOfSequences = m;
	}

	private void setPointers(String in) throws IOException {
        long numCharsToSkip = (LINES_IN_BLOCK * 53) * (bufferSize);
        int curPos = 0;
        long i = 0;
        while(i < numOfSequences){
			BufferedReader pointer = new BufferedReader(new FileReader(in), BUFFER_SIZE);
			pointers.add(pointer);
			long s = pointer.skip(curPos);
			curPos += numCharsToSkip;
			++i;
		}

    }

    private int findMinIndex(){
    	// to find the first non-empty string
	    String min = "";
		int minIndex = -1;
		for(int i = 0; i < memory.size() - 1; ++i){
			if(memory.get(i) != null) {
				if (memory.get(i).size() != 0) {
					min = memory.get(i).getFirst();
					minIndex = i;
					break;
				}
			}
		}

	    for(int i = 0; i < memory.size() - 1; ++i){
	    	if(memory.get(i) != null){
	    		if(memory.get(i).size() != 0){
					String temp = memory.get(i).getFirst();
					if(temp.equals("") || temp.equals("\n"))
						continue;
					if(compare(min, temp) > 0){
						min  = temp;
						minIndex = i;
					}
				}
			}
        }
	    return minIndex;

    }
	private void phase2Sort(String in, BufferedWriter writer) throws IOException {

        // first part
        //setPointers(in);
		System.out.println("in phase 2");
        for(int i = 0; i < pointers.size(); ++i){
        	ArrayDeque<String> block = readBlock(pointers.get(i));
            memory.add(block);
        }
		memory.add(new ArrayDeque<>());

        // second part
        int k = 0;
        boolean endFile = false;
		ArrayDeque<String> outputBlock = null;
        while( k < LINES_IN_BLOCK * numBlocks && !endFile) {
            if(k % 100000 == 0)
        	    System.out.println("iteration " + k + " out of " + LINES_IN_BLOCK * numBlocks);
            int index = findMinIndex();
            if(index == -1)
            	System.exit(4);
            outputBlock = memory.get(memory.size() - 1);
            outputBlock.add(memory.get(index).removeFirst());
            ++k;
            if(k % (LINES_IN_BLOCK) == 0){
                writeBlock(writer, outputBlock);
                outputBlock.clear();
            }
            if(memory.get(index).size() == 0){
            	ArrayDeque<String> block = readBlock(pointers.get(index));
            	if(block.size() == 0){
					endFile = true;
					memory.set(index, null);
				}
            	else
                	memory.set(index, block);
            }
        }
        if(endFile){
			writeBlock(writer, outputBlock);
			outputBlock.clear();
		}


	}

	@Override
	public void sort(String in, String out, String tmpPath) {
		
		File fileIn = new File(in);
		File fileOut = new File(out);
		File fileTmp = new File(tmpPath);
		tmp = fileTmp;

		BufferedReader reader = null;
		BufferedWriter writer = null;
		BufferedWriter tmpWriter1 = null;
		BufferedReader tmpReader1 = null;
		BufferedWriter tmpWriter2 = null;
		BufferedReader tmpReader2 = null;
		File tempFile1 = null;
		File tempFile2 = null;

		try {
			reader = new BufferedReader(new FileReader(fileIn), BUFFER_SIZE);
			writer = new BufferedWriter(new FileWriter(fileOut), BUFFER_SIZE);
			tempFile1 = File.createTempFile("tmp0#1", ".txt", fileTmp);
			tempFile1.deleteOnExit();
			tmpWriter1 = new BufferedWriter(new FileWriter(tempFile1));
			tmpReader1 = new BufferedReader(new FileReader(tempFile1));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		long fileLength = fileIn.length();
		numCharsInFile = fileLength;
		fileLength = (((fileLength - LINE_SIZE ) / (LINE_SIZE + 1)) + 1) * LINE_SIZE; // get the size of the file without \n's
		this.numBlocks = fileLength / BLOCK_SIZE + 1;
		this.bufferSize = RAM_SIZE / BLOCK_SIZE;
		memory = new ArrayList<>();

        try {
			phase1Sort(reader, tmpWriter1, this.bufferSize, this.numBlocks);
        	tmpWriter1.close();
        	reader.close();
            phase2Sort(fileTmp.getName() + "/" + tempFile1.getName(), writer);
            reader.close();
            tmpReader1.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeJoinTuple(BufferedWriter writer, String tr, String ts, String subStrSelect) throws IOException {
    	String[] trElems = tr.split(" ");
    	String[] tsElems = ts.split(" ");
    	StringBuilder res = new StringBuilder();
    	if(subStrSelect != null){
    		if(!trElems[0].contains(subStrSelect)){
				return;
			}
		}
		res.append(trElems[0]);
    	res.append(' ');
		res.append(trElems[1]);
		res.append(' ');
		res.append(trElems[2]);
		res.append(' ');
		res.append(tsElems[1]);
		res.append(' ');
		res.append(tsElems[2]);
		writer.write(res.toString());
		writer.write('\n');



	}

	@Override
	protected void join(String in1, String in2, String out, String tmpPath) {
		joinWithOptionalSelect(in1, in2, out, null, tmpPath);
	}

	@Override
	protected void select(String in, String out, String substrSelect, String tmpPath) {

		File fileIn = new File(in);
		File fileOut = new File(out);
		File fileTmpPath = new File(tmpPath);

		BufferedReader reader = null;
		BufferedWriter writer = null;

		try {
			reader = new BufferedReader(new FileReader(in));
			writer = new BufferedWriter(new FileWriter(out));

			String line = reader.readLine();
			while( line != null){
				String[] elems = line.split(" ");
				if(elems[0].contains(substrSelect)){
					writer.write(line);
					writer.write('\n');
				}
				line = reader.readLine();

			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void joinAndSelectEfficiently(String in1, String in2, String out,
			String substrSelect, String tmpPath) {
    	File sortedFile1 = null;
    	File sortedFile2 = null;
    	File temp = new File(tmpPath);
		try {
			sortedFile1 = File.createTempFile("sorted001", ".txt", temp);
			sortedFile1.deleteOnExit();
			sortedFile2 = File.createTempFile("sorted002", ".txt", temp);
			sortedFile2.deleteOnExit();
			this.sort(in1, sortedFile1.getPath(), tmpPath);
			this.sort(in2, sortedFile2.getPath(), tmpPath);
			joinWithOptionalSelect(sortedFile1.getPath(), sortedFile2.getPath(), out, substrSelect, tmpPath);
		} catch (IOException e) {
			e.printStackTrace();
		}

		
	}

	private void joinWithOptionalSelect (String in1, String in2, String out,
										 String substrSelect, String tmpPath) {
		File fileIn1 = new File(in1);
		File fileIn2 = new File(in2);
		File fileOut = new File(out);
		File fileTmpPath = new File(tmpPath);

		BufferedReader trReader = null;
		BufferedReader tsReader = null;
		BufferedReader gsReader = null;
		BufferedWriter writer = null;

		try {
			trReader = new BufferedReader(new FileReader(fileIn1));
			tsReader = new BufferedReader(new FileReader(fileIn2));
			gsReader = new BufferedReader(new FileReader(fileIn2));
			writer = new BufferedWriter(new FileWriter(fileOut));
			int trLineCounter = 1; // the cursor is on this line
			int tsLineCounter = 1; // the cursor is on this line
			int gsLineCounter = 1; // the cursor is on this line

			String tr = trReader.readLine();
			String ts = tsReader.readLine();
			String gs = gsReader.readLine();

			trLineCounter++;
			tsLineCounter++;
			gsLineCounter++;

			while(tr != null && gs != null) {

				// while Tr != eof and Tr[E]<Gs[E]:
				// increment Tr
				while (tr != null && compareForJoin(tr, gs) < 0) {
					tr = trReader.readLine();
					trLineCounter++;
				}

				// while Gs != eof and Tr[E]>Gs[E]:
				// increment Gs
				while (gs != null && compareForJoin(tr, gs) > 0) {
					gs = gsReader.readLine();
					trLineCounter++;
				}

				// while Tr != eof and Tr[E] == Gs[E]:
				while (tr != null && compareForJoin(tr, gs) == 0) {

					// Ts = Gs
					while (tsLineCounter < gsLineCounter) {
						ts = tsReader.readLine();
						tsLineCounter++;
					}

					// while Ts != eof and Ts[E] == Tr[E]:
					// add (Tr,Ts) to output
					// increment Ts
					while (ts != null && compareForJoin(tr, ts) == 0) {

						writeJoinTuple(writer, tr, ts, substrSelect);

						ts = tsReader.readLine();
						tsLineCounter++;
					}

					// increment Tr
					tr = trReader.readLine();
					trLineCounter++;
				}
				// Gs = Ts
				while (gsLineCounter < tsLineCounter) {
					gs = gsReader.readLine();
					gsLineCounter++;
				}
			}
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}
}
